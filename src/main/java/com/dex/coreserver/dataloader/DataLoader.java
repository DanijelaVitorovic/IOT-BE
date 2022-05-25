package com.dex.coreserver.dataloader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader extends DataLoaderBasic implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        loadBasicData();
    }
}

