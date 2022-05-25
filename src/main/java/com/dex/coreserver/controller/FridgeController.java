package com.dex.coreserver.controller;

import com.dex.coreserver.model.Fridge;
import com.dex.coreserver.service.FridgeService;
import com.dex.coreserver.service.MapValidationErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/fridge")
public class FridgeController extends BasicController {
    @Autowired
    private FridgeService fridgeService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @Override
    protected String getClassName() {
        return Fridge.class.getName();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Fridge fridge, BindingResult result, Principal principal,
                                    @RequestHeader(value = "locale", required = false) String locale) {
        mapValidationErrorService.MapValidationService(result);
        Fridge createdFridge = fridgeService.create(fridge, principal.getName());
        return new ResponseEntity<>(createdFridge, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody Fridge fridge, BindingResult result, Principal principal,
                                    @RequestHeader(value = "locale", required = false) String locale) {
        mapValidationErrorService.MapValidationService(result);
        Fridge updatedFridge = fridgeService.update(fridge, principal.getName());
        return new ResponseEntity<>(updatedFridge, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> findAll(Principal principal, @RequestHeader(value = "locale", required = false) String locale) {
        List<Fridge> fridgeList = fridgeService.findAll(principal.getName());
        return new ResponseEntity<>(fridgeList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id, Principal principal,
                                      @RequestHeader(value = "locale", required = false) String locale) {
        Fridge fridge = fridgeService.findById(id);
        return new ResponseEntity<>(fridge, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id, Principal principal,
                       @RequestHeader(value = "locale", required = false) String locale) {
        fridgeService.delete(id, principal.getName());
    }

    @GetMapping("/{pageNumber}/{pageSize}")
    public ResponseEntity<?> findAllPageable(@PathVariable int pageNumber, @PathVariable int pageSize) {
        Page page = fridgeService.findAllByPageAndSize(pageNumber, pageSize);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }
}
