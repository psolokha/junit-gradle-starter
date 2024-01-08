package ru.pstest.junit;

import org.junit.jupiter.api.extension.ExtendWith;
import ru.pstest.junit.extension.GlobalExtension;

@ExtendWith({
        GlobalExtension.class
})
public abstract class TestBase {

}
