package com.example.boilerplate.service.impl;

import com.example.boilerplate.dto.elasticSearch.UploadModel;
import com.example.boilerplate.entity.Book;
import com.example.boilerplate.entity.Genre;
import com.example.boilerplate.entity.User;
import com.example.boilerplate.entity.Writer;
import com.example.boilerplate.entity.elasticSearch.IndexUnit;
import com.example.boilerplate.handlers.PDFHandler;
import com.example.boilerplate.repository.IBookRepository;
import com.example.boilerplate.repository.IGenreRepository;
import com.example.boilerplate.repository.IUserRepository;
import com.example.boilerplate.repository.IWriterRepository;
import com.example.boilerplate.repository.elasticSearch.IIndexUnitRepository;
import com.example.boilerplate.service.IBookService;
import com.example.boilerplate.service.elasticSearch.IndexerService;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BookService implements IBookService {

    private final IBookRepository bookRepository;

    private final IGenreRepository genreRepository;

    private final IWriterRepository writerRepository;

    private final IUserRepository userRepository;

    private final IndexerService indexerService;

    private final IIndexUnitRepository indexUnitRepository;

    private static String FOLDER = "src\\main\\resources\\files\\";

    public BookService(IBookRepository bookRepository,
        IGenreRepository genreRepository, IWriterRepository writerRepository,
        IUserRepository userRepository, IndexerService indexerService,
        IIndexUnitRepository indexUnitRepository) {
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
        this.writerRepository = writerRepository;
        this.userRepository = userRepository;
        this.indexerService = indexerService;
        this.indexUnitRepository = indexUnitRepository;
    }

    @Override
    public void createBook(MultipartFile files, String title, String keyWords, String writerId, String genresIds)
        throws IOException {
        Book book = new Book();
        book.setTitle(title);
        book.setKeyWords(keyWords);

        User user = userRepository.findOneById(UUID.fromString(writerId)).get();
        Writer writer = user.getWriter();
        book.setWriter(writer);

        //TODO: check if there is only one file for the book

        int i = 1;
        MultipartFile array[] = new MultipartFile[1];
        List<MultipartFile> filesList = new ArrayList<>();
        filesList.add(files);
        for (MultipartFile file : filesList) {
            array[i - 1] = file;
            try {
                byte[] bytes = file.getBytes();

                File newFile = new File(FOLDER);
                if (!newFile.exists()) {
                    newFile.mkdir();
                }

                File check = new File(FOLDER + "\\" + file.getOriginalFilename());

                if (check.exists()) {
                    //TODO: Handle exception
                }

                if (!file.getOriginalFilename().contains(".pdf")) {
                    //TODO: Handle exception
                }

                Path path = Paths.get(newFile + "\\" + file.getOriginalFilename());
                Files.write(path, bytes);

                if (i == filesList.size()) {
                    book.setPath(path.toString());
                    break;
                }
                book.setPath(path.toString() + ",");
                i++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String genres[] = genresIds.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        for (String id : genres) {
            Genre genre = genreRepository.findOneById(UUID.fromString(id)).get();
            book.getGenres().add(genre);
            stringBuilder.append(genre.getName()).append(" ");
        }

        Book createdBook = bookRepository.save(book);

        writer.getBooks().add(createdBook);
        writerRepository.save(writer);

        try {
            UploadModel uploadModel = new UploadModel(title, keyWords, stringBuilder.toString(),
                writer.getUser().getFirstName() + " " + writer.getUser().getLastName(),
                array);
            indexUploadedFile(uploadModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    //save file
    private String saveUploadedFile(MultipartFile file) throws IOException {
        String retVal = null;
        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            Path path = Paths
                .get(getResourceFilePath("files").getAbsolutePath() + File.separator + file.getOriginalFilename());
            Files.write(path, bytes);
            retVal = path.toString();
        }
        return retVal;
    }

    private void indexUploadedFile(UploadModel model) throws IOException {

        for (MultipartFile file : model.getFiles()) {

            if (file.isEmpty()) {
                continue; //next please
            }
            String fileName = saveUploadedFile(file);
            if (fileName != null) {
                PDFHandler pdfHandler = new PDFHandler();
                IndexUnit indexUnit = pdfHandler.getIndexUnit(new File(fileName));
                indexUnit.setTitle(model.getTitle());
                indexUnit.setKeywords(model.getKeywords());
                indexUnit.setGenres(model.getGenres());
                indexUnit.setWriter(model.getWriter());
                boolean flag = indexerService.add(indexUnit);
                if(flag){
                    indexUnitRepository.save(indexUnit);
                }
            }
        }
    }
}
