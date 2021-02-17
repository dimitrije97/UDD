package com.example.boilerplate.service;

import com.example.boilerplate.entity.Book;
import com.example.boilerplate.entity.Genre;
import com.example.boilerplate.entity.User;
import com.example.boilerplate.entity.Writer;
import com.example.boilerplate.repository.IBookRepository;
import com.example.boilerplate.repository.IGenreRepository;
import com.example.boilerplate.repository.IUserRepository;
import com.example.boilerplate.repository.IWriterRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private static String FOLDER = "src\\main\\resources\\files\\";

    public BookService(IBookRepository bookRepository,
        IGenreRepository genreRepository, IWriterRepository writerRepository,
        IUserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
        this.writerRepository = writerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createBook(List<MultipartFile> files, String title, String keyWords, String writerId, String genresIds)
        throws IOException {
        Book book = new Book();
        book.setTitle(title);
        book.setKeyWords(keyWords);

        User user = userRepository.findOneById(UUID.fromString(writerId)).get();
        Writer writer = user.getWriter();
        book.setWriter(writer);

        int i = 1;
        for (MultipartFile file : files) {
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

                if(!file.getOriginalFilename().contains(".pdf")){
                    //TODO: Handle exception
                }

                Path path = Paths.get(newFile + "\\" + file.getOriginalFilename());
                Files.write(path, bytes);

                if (i == files.size()) {
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
        for (String id : genres) {
            Genre genre = genreRepository.findOneById(UUID.fromString(id)).get();
            book.getGenres().add(genre);
        }

        Book createdBook = bookRepository.save(book);

        writer.getBooks().add(createdBook);
        writerRepository.save(writer);
    }
}
