package com.example.boilerplate.controller.elasticSearch;

import com.example.boilerplate.dto.elasticSearch.SimpleQuery;
import com.example.boilerplate.dto.elasticSearch.UploadModel;
import com.example.boilerplate.entity.elasticSearch.IndexUnit;
import com.example.boilerplate.handlers.PDFHandler;
import com.example.boilerplate.service.elasticSearch.IndexerService;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/indexer")
public class IndexerController {

    private final IndexerService indexerService;

    public IndexerController(IndexerService indexerService) {
        this.indexerService = indexerService;
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
}
