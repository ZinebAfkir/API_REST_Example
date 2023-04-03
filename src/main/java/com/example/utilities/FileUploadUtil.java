package com.example.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component //para crear los beans que haga falta a partir de esta clase (objetos de esta clase)
public class FileUploadUtil {
    
    //para poder usar el metodo saveFile

    public  String saveFile(String fileName, MultipartFile multipartFile) //MultipartFile son los trozos del archivo que luego va a componer
            throws IOException {
        Path uploadPath = Paths.get("Files-Upload");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileCode = RandomStringUtils.randomAlphanumeric(8); //el codigo alfanumerico se genera con esta linea, se genera uno codigo random alfanumerico de 8 caracteres

        // Try-with-resources
        // Los recursos que se pueden manejar son los que implementan la interfaz closeable
        // closeable
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileCode + "-" + fileName); 
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING); //Aqui se copio el archivo //REPLACE... por si existe o coincida un archivo igual me lo replaza
        } catch (IOException ioe) {
            throw new IOException("Could not save file: " + fileName, ioe);
        } 

        

        return fileCode;
    }
}
