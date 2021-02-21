package com.example.boilerplate.service.elasticSearch;

import com.example.boilerplate.entity.elasticSearch.IndexUnit;
import com.example.boilerplate.handlers.PDFHandler;
import com.example.boilerplate.repository.elasticSearch.IIndexUnitRepository;
import java.io.File;
import org.springframework.stereotype.Service;

@Service
public class IndexerService {

    private final IIndexUnitRepository indexUnitRepository;

    public IndexerService(IIndexUnitRepository indexUnitRepository) {
        this.indexUnitRepository = indexUnitRepository;
    }

    public boolean delete(String filename) {
        if (indexUnitRepository.findById("C:\\Users\\Lara\\Desktop\\Master\\UDD\\UDD\\target\\classes\\files\\" + filename)
            .isPresent()) {
            File fileInSrc = new File("src\\main\\resources\\files\\" + filename);
            File fileInTarget = new File("target\\classes\\files\\" + filename);
            if (fileInSrc.exists()) {
                fileInSrc.delete();
            }
            if (fileInTarget.exists()) {
                fileInTarget.delete();
            }
            indexUnitRepository.delete(indexUnitRepository
                .findById("C:\\Users\\Lara\\Desktop\\Master\\UDD\\UDD\\target\\classes\\files\\" + filename).get());
            return true;
        } else {
            return false;
        }
    }

    public boolean update(IndexUnit unit) {
        unit = indexUnitRepository.save(unit);
        if (unit != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean add(IndexUnit unit) {
        unit = indexUnitRepository.index(unit);
        if (unit != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param file Direktorijum u kojem se nalaze dokumenti koje treba indeksirati
     */
    public int index(File file) {
        PDFHandler handler = new PDFHandler();
        int retVal = 0;
        try {
            File[] files;
            if (file.isDirectory()) {
                files = file.listFiles();
            } else {
                files = new File[1];
                files[0] = file;
            }
            for (File newFile : files) {
                if (newFile.isFile()) {
                    if (add(handler.getIndexUnit(newFile))) {
                        retVal++;
                    }
                } else if (newFile.isDirectory()) {
                    retVal += index(newFile);
                }
            }
            System.out.println("indexing done");
        } catch (Exception e) {
            System.out.println("indexing NOT done");
        }
        return retVal;
    }
}
