package org.example.samples;

public class SpaceBetweenVariableBlock {
    public String foo = "" +
            "";

    @Autowired
    public String foo1;
    @LazyAutowired
    public String foo2;
}
