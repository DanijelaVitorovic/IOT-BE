package com.dex.coreserver.util;

import com.dex.coreserver.model.enums.Actions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Service
public class ApplicationUtils {

    @Value( "${spring.profiles.active}" )
    private String activeProfile;

    public static final String ROLE_ADMIN = "admin";

    public static final String allowedFileFormats = ".pdf, .png, .jpg, .gif, .xlsx, .csv, .docx, .doc, " +
            ".txt, .mp4, .mov, .wmv, .avi";

    public String getFilePath(){
        ResourceBundle applicationRB = ResourceBundle.getBundle("application-" + activeProfile);
        String defaultPath = null;
        try{
            defaultPath = applicationRB.getString("file.path");
        }catch (Exception e){
            defaultPath = "C:/CoreApp/";
        }
        return defaultPath;
    }

    public String getApplicationPropsByKey(String key){
        ResourceBundle appProps = ResourceBundle.getBundle("application-" + activeProfile);
        return appProps.getString( key );
    }

    public static Page toPage(List<?> list, int pageNumber, int pageSize) {
        int totalPages = list.size() / pageSize;
        int max = pageNumber>=totalPages? list.size():pageSize*(pageNumber+1);
        int min = pageNumber >totalPages? max:pageSize*pageNumber;
        return new PageImpl<>(list.subList(min, max), PageRequest.of(pageNumber, pageSize), list.size());
    }

    public static List<Actions> filterActionsList(List<Actions> actions){
        if(!actions.isEmpty() && DescriptionUtils.getAppName() != null){
           return actions.stream()
                   .filter(action -> action.getActionAppVersion() == null
                           || action.getActionAppVersion().contains(DescriptionUtils.getAppName()))
                    .collect(Collectors.toList());
        }
        return actions;
    }

    public static final String FILTER_CLIENT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss+02:00";

    public static final String DEFAULT_REGEX = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[!@#$%^&*_+-]).{8,}$";
    public static final String DEFAULT_REGEX_DESCRIPTION = "Minimum: jedno veliko slovo, jedno malo slovo, jedan broj, " +
            "jedan specijalni karakter i dužina treba da bude minimum 8 karaktera";
}
