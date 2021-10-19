import lombok.Getter;
import lombok.Setter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PCSOProperties {

    private static PCSOProperties single_instance = null;

    @Getter
    @Setter
    private Properties propertiesFile = new Properties();

    private PCSOProperties() throws IOException {
        FileReader reader = new FileReader("pcso.properties");
        propertiesFile.load(reader);
    }

    public static PCSOProperties getInstance() throws IOException {
        if (single_instance == null)
            single_instance = new PCSOProperties();

        return single_instance;
    }


}
