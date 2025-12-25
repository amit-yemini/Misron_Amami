package msa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheInitializer implements ApplicationRunner {

    @Autowired
    private List<CacheLoader> loaders;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loaders.forEach(CacheLoader::load);
    }
}
