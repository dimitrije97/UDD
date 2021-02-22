package com.example.boilerplate.controller.elasticSearch;

import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageForwardRequest;
import com.example.boilerplate.dto.elasticSearch.UploadModel;
import com.example.boilerplate.entity.elasticSearch.IndexUnit;
import com.example.boilerplate.entity.elasticSearch.ReviewerIndexUnit;
import com.example.boilerplate.handlers.PDFHandler;
import com.example.boilerplate.repository.elasticSearch.IReviewerIndexUnitRepository;
import com.example.boilerplate.service.elasticSearch.IndexerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/indexer")
public class IndexerController {

    private final IndexerService indexerService;

    private final IReviewerIndexUnitRepository reviewerIndexUnitRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestHighLevelClient client;

    public IndexerController(IndexerService indexerService,
        IReviewerIndexUnitRepository reviewerIndexUnitRepository) {
        this.indexerService = indexerService;
        this.reviewerIndexUnitRepository = reviewerIndexUnitRepository;
    }

    @GetMapping("/reindex")
    public ResponseEntity<String> index() throws IOException {
        File dataDir = getResourceFilePath("files");
        long start = new Date().getTime();
        int numIndexed = indexerService.index(dataDir);
        long end = new Date().getTime();
        String text = "Indexing " + numIndexed + " files took "
            + (end - start) + " milliseconds";
        return new ResponseEntity<String>(text, HttpStatus.OK);
    }

    private File getResourceFilePath(String path) {
        URL url = this.getClass().getClassLoader().getResource(path);
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        }
        return file;
    }



    @PostMapping("/add")
    public ResponseEntity<String> multiUploadFileModel(@ModelAttribute UploadModel model) {


        try {

            indexUploadedFile(model);

        } catch (IOException e) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>("Successfully uploaded!", HttpStatus.OK);

    }

    @PostMapping("/add-reviewer")
    public void addReviewer(@RequestParam String email, @RequestParam String city, @RequestParam String country) throws Exception {
        try {
            indexReviewer(email, city, country);
        } catch (Exception e) {
            throw new Exception();
        }
    }

    @DeleteMapping("/delete/{filename}")
    public void deleteFile(@PathVariable String filename){
        indexerService.delete(filename);
    }


    //save file
    private String saveUploadedFile(MultipartFile file) throws IOException {
        String retVal = null;
        if (! file.isEmpty()) {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(getResourceFilePath("files").getAbsolutePath() + File.separator + file.getOriginalFilename());
            Files.write(path, bytes);
            retVal = path.toString();
        }
        return retVal;
    }

    private void indexUploadedFile(UploadModel model) throws IOException{

        for (MultipartFile file : model.getFiles()) {

            if (file.isEmpty()) {
                continue; //next please
            }
            String fileName = saveUploadedFile(file);
            if(fileName != null){
                PDFHandler pdfHandler = new PDFHandler();
                IndexUnit indexUnit = pdfHandler.getIndexUnit(new File(fileName));
                indexUnit.setTitle(model.getTitle());
                indexUnit.setKeywords(model.getKeywords());
                indexUnit.setWriter(model.getWriter());
                indexUnit.setGenres(model.getGenres());
                indexerService.add(indexUnit);
            }
        }
    }

    public boolean add(ReviewerIndexUnit unit) {
        unit = reviewerIndexUnitRepository.index(unit);
        if(unit != null){
            return true;
        }else {
            return false;
        }
    }

    private ReviewerIndexUnit indexReviewer(String email, String city, String country) throws IOException {

        ReviewerIndexUnit reviewerIndexUnit = new ReviewerIndexUnit();
        reviewerIndexUnit.setEmail(email);
        var jOpenCageGeocoder = new JOpenCageGeocoder("b0fc7df42d5e4b73ac60115bb7e0c7db");
        var jOpenCageForwardRequest = new JOpenCageForwardRequest(city + ", " + country);
        jOpenCageForwardRequest.setMinConfidence(1);
        jOpenCageForwardRequest.setNoAnnotations(false);
        jOpenCageForwardRequest.setNoDedupe(true);
        var jOpenCageResponse = jOpenCageGeocoder.forward(jOpenCageForwardRequest);
        reviewerIndexUnit.setLocation(new GeoPoint(jOpenCageResponse.getResults().get(0).getGeometry().getLat(), jOpenCageResponse.getResults().get(0).getGeometry().getLng()));

        Map<String, Object> documentMapper = objectMapper.convertValue(reviewerIndexUnit, Map.class);

        documentMapper.put("location", reviewerIndexUnit.getLocation().getLat() + "," + reviewerIndexUnit.getLocation().getLon());

        IndexRequest indexRequest = new IndexRequest("reviewer-unit").id(reviewerIndexUnit.getEmail()).source(documentMapper);

        client.index(indexRequest, RequestOptions.DEFAULT);

        return reviewerIndexUnit;
    }
}
