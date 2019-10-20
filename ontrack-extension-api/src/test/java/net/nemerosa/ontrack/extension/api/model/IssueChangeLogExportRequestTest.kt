package net.nemerosa.ontrack.extension.api.model;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IssueChangeLogExportRequestTest {

    @Test
    public void empty() {
        IssueChangeLogExportRequest request = new IssueChangeLogExportRequest();
        Map<String, Set<String>> spec = request.getGroupingSpecification();
        assertTrue(spec.isEmpty());
    }

    @Test
    public void one_group() {
        IssueChangeLogExportRequest request = new IssueChangeLogExportRequest();
        request.setGrouping("Bugs=bug");
        Map<String, Set<String>> spec = request.getGroupingSpecification();
        assertEquals(1, spec.size());
        assertEquals(Sets.newHashSet("bug"), spec.get("Bugs"));
    }

    @Test
    public void one_group_with_several_values() {
        IssueChangeLogExportRequest request = new IssueChangeLogExportRequest();
        request.setGrouping("Features=feature,enhancement");
        Map<String, Set<String>> spec = request.getGroupingSpecification();
        assertEquals(1, spec.size());
        assertEquals(Sets.newHashSet("feature", "enhancement"), spec.get("Features"));
    }

    @Test
    public void two_groups() {
        IssueChangeLogExportRequest request = new IssueChangeLogExportRequest();
        request.setGrouping("Bugs=bug|Features=feature,enhancement");
        Map<String, Set<String>> spec = request.getGroupingSpecification();
        assertEquals(2, spec.size());
        assertEquals(Sets.newHashSet("bug"), spec.get("Bugs"));
        assertEquals(Sets.newHashSet("feature", "enhancement"), spec.get("Features"));
    }

    @Test(expected = ExportRequestGroupingFormatException.class)
    public void group_format_exception_equal_sign() {
        IssueChangeLogExportRequest request = new IssueChangeLogExportRequest();
        request.setGrouping("Bugs=bug=test");
        request.getGroupingSpecification();
    }

    @Test
    public void getExcludedTypes_empty() {
        IssueChangeLogExportRequest request = new IssueChangeLogExportRequest();
        request.setExclude("");
        assertTrue(request.getExcludedTypes().isEmpty());
    }

    @Test
    public void getExcludedTypes_blank() {
        IssueChangeLogExportRequest request = new IssueChangeLogExportRequest();
        request.setExclude("  ");
        assertTrue(request.getExcludedTypes().isEmpty());
    }

    @Test
    public void getExcludedTypes_one() {
        IssueChangeLogExportRequest request = new IssueChangeLogExportRequest();
        request.setExclude("type1");
        assertEquals(
                Sets.newHashSet("type1"),
                request.getExcludedTypes());
    }

    @Test
    public void getExcludedTypes_one_trimmed() {
        IssueChangeLogExportRequest request = new IssueChangeLogExportRequest();
        request.setExclude(" type1  ");
        assertEquals(
                Sets.newHashSet("type1"),
                request.getExcludedTypes());
    }

    @Test
    public void getExcludedTypes_several() {
        IssueChangeLogExportRequest request = new IssueChangeLogExportRequest();
        request.setExclude("type1,type2,type3");
        assertEquals(
                Sets.newHashSet("type1", "type2", "type3"),
                request.getExcludedTypes());
    }

    @Test
    public void getExcludedTypes_several_trimmed() {
        IssueChangeLogExportRequest request = new IssueChangeLogExportRequest();
        request.setExclude(" type1  ,  type2 ,    type3   ");
        assertEquals(
                Sets.newHashSet("type1", "type2", "type3"),
                request.getExcludedTypes());
    }

}
