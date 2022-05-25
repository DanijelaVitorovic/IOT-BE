package com.dex.coreserver.util;

import com.dex.coreserver.exceptions.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class DocumentUtils {

    @Autowired
    private ApplicationUtils applicationUtils;

    public static byte[] transformInputStreamInByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }

    public static String[] getUUIDandMymeTypeShort(MultipartFile multipartFile) {
        String[] documentValues = new String[2];
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        if(multipartFile.getContentType() == null) throw new AppException( "FILE_FORMAT_IS_NULL" );
        switch(multipartFile.getContentType()) {
            case "application/pdf":
                documentValues[0] = randomUUIDString + ".pdf";
                documentValues[1] = ".pdf";
                break;
            case "image/png":
                documentValues[0] = randomUUIDString + ".png";
                documentValues[1] = ".png";
                break;
            case "image/jpeg":
                documentValues[0] = randomUUIDString + ".jpg";
                documentValues[1] = ".jpg";
                break;
            case "image/gif":
                documentValues[0] = randomUUIDString + ".gif";
                documentValues[1] = ".gif";
                break;
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                documentValues[0] = randomUUIDString + ".xlsx";
                documentValues[1] = ".xlsx";
                break;
            case "application/vnd.ms-excel":
                documentValues[0] = randomUUIDString + ".csv";
                documentValues[1] = ".csv";
                break;
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                documentValues[0] = randomUUIDString + ".docx";
                documentValues[1] = ".docx";
                 break;
            case "application/msword":
                documentValues[0] = randomUUIDString + ".doc";
                documentValues[1] = ".doc";
                 break;
            case "text/plain":
                documentValues[0] = randomUUIDString + ".txt";
                documentValues[1] = ".txt";
                 break;
            case "video/mp4":
                documentValues[0] = randomUUIDString + ".mp4";
                documentValues[1] = ".mp4";
                 break;
            case "video/quicktime":
                documentValues[0] = randomUUIDString + ".mov";
                documentValues[1] = ".mov";
                 break;
            case "video/x-ms-wmv":
                documentValues[0] = randomUUIDString + ".wmv";
                documentValues[1] = ".wmv";
                 break;
            case "video/x-msvideo":
                documentValues[0] = randomUUIDString + ".avi";
                documentValues[1] = ".avi";
                break;
            default:
                break;
        }
        return documentValues;
    }

    public void saveDocumentOnDisc(String randomUUIDString, MultipartFile multipartFile) {
        Path targetPath = Paths.get(applicationUtils.getFilePath() + randomUUIDString);
        try (InputStream input = multipartFile.getInputStream()) {
            Files.copy(input, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
           throw new AppException("FILE_PATH_NOT_FOUND");
        }
    }

    public static void downloadFile(String pathToFile, HttpServletResponse response) {
        File file = new File(pathToFile);
        if (file.exists()) {
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            response.setContentType(mimeType);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            response.setContentLength((int) file.length());
            try {
                InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                FileCopyUtils.copy(inputStream, response.getOutputStream());
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        else{
            throw new AppException("DOWNLOAD_FAILED");
        }
    }

}
