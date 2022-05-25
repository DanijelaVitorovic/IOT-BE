package com.dex.coreserver.service;

import com.dex.coreserver.model.AbstractDataModel;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BasicAttachmentService<T extends AbstractDataModel> {
    T findByDocumentName(String parentName);
    T upload(MultipartFile uploadFile, Long parentId, String username);
    T findByLatestVersion(Long parentId);
    List<T> findAllByDocumentId(Long parentId);
    Page findAllByDocumentIdAndPageAndSize(Long parentId, int pageNumber, int pageSize);
}
