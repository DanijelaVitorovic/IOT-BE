package com.dex.coreserver.controller;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Data
@NoArgsConstructor
public abstract class BasicController {

    protected final Logger logger = LogManager.getLogger(getClassName());
    protected abstract String getClassName();
}
