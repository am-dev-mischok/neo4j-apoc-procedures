package apoc.util;

import org.junit.rules.TemporaryFolder;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.test.TestDatabaseManagementServiceBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static apoc.ApocConfig.SUN_JAVA_COMMAND;
import static org.neo4j.configuration.GraphDatabaseSettings.procedure_unrestricted;

public class DbmsTestUtil {

    public static DatabaseManagementService startDbWithApocConfs(TemporaryFolder storeDir, Map<String, Object> configMap) throws IOException {
        // Used `new File(..).createNewFile()` instead of storeDir.newFile(..)
        // because the latter throws an IOException if the file already exists
        File configFile = new File(storeDir.getRoot(), "apoc.conf");
        configFile.createNewFile();
        try (FileWriter writer = new FileWriter(configFile)) {
            // `key=value` lines in apoc.conf file
            String confString = configMap.entrySet()
                    .stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("\n"));

            writer.write(confString);
        }
        System.setProperty(SUN_JAVA_COMMAND, "config-dir=" + storeDir.getRoot().getAbsolutePath());

        return new TestDatabaseManagementServiceBuilder(storeDir.getRoot().toPath())
                .setConfig(procedure_unrestricted, List.of("apoc*"))
                .build();
    }
}
