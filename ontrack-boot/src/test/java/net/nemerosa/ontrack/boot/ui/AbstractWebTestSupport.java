package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.Application;
import net.nemerosa.ontrack.it.AbstractITTestSupport;
import org.springframework.boot.test.SpringApplicationConfiguration;

@SpringApplicationConfiguration(classes = Application.class)
public abstract class AbstractWebTestSupport extends AbstractITTestSupport {

}
