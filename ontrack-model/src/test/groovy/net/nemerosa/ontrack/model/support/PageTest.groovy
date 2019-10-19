package net.nemerosa.ontrack.model.support

import org.junit.Test

class PageTest {

    private final List<String> list = [
            "Zero",
            "One",
            "Two",
            "Three",
            "Four",
            "Five",
            "Six",
            "Seven",
    ]

    @Test
    void 'Sub list from 0'() {
        assert new Page(0, 4).extract(list) == [
                "Zero",
                "One",
                "Two",
                "Three",
        ]
    }

    @Test
    void 'Sub list from 0 to end'() {
        assert new Page(0, 8).extract(list) == [
                "Zero",
                "One",
                "Two",
                "Three",
                "Four",
                "Five",
                "Six",
                "Seven",
        ]
    }

    @Test
    void 'Sub list from 0 to beyond end'() {
        assert new Page(0, 12).extract(list) == [
                "Zero",
                "One",
                "Two",
                "Three",
                "Four",
                "Five",
                "Six",
                "Seven",
        ]
    }

    @Test
    void 'Sub list from middle'() {
        assert new Page(4, 2).extract(list) == [
                "Four",
                "Five",
        ]
    }

    @Test
    void 'Sub list from middle to end'() {
        assert new Page(4, 4).extract(list) == [
                "Four",
                "Five",
                "Six",
                "Seven",
        ]
    }

    @Test
    void 'Sub list from middle to beyond end'() {
        assert new Page(4, 8).extract(list) == [
                "Four",
                "Five",
                "Six",
                "Seven",
        ]
    }

    @Test
    void 'Sub list from beyond end'() {
        assert new Page(8, 2).extract(list) == []
    }

}
