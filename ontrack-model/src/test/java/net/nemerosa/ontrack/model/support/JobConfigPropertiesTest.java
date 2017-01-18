package net.nemerosa.ontrack.model.support;

import org.junit.Test;

public class JobConfigPropertiesTest {

    @Test(expected = IllegalArgumentException.class)
    public void scatteringRatioMustBeGteZero() {
        JobConfigProperties properties = new JobConfigProperties();
        properties.setScatteringRatio(-0.01);
    }

    @Test
    public void scatteringRatioZeroIsOK() {
        JobConfigProperties properties = new JobConfigProperties();
        properties.setScatteringRatio(0);
        properties.setScatteringRatio(0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void scatteringRatioMustBeGLteOne() {
        JobConfigProperties properties = new JobConfigProperties();
        properties.setScatteringRatio(1.01);
    }

    @Test
    public void scatteringRatioOneIsOK() {
        JobConfigProperties properties = new JobConfigProperties();
        properties.setScatteringRatio(1);
        properties.setScatteringRatio(1.0);
    }

}
