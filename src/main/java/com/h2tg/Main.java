package com.h2tg;

import com.h2tg.templates.Example4java2;
import com.h2tg.templates.PatchTemplate;

public class Main
{
    public static void main(String[] args)
    {
        PatchTemplate patch = new Example4java2("example/java2.jar");
        patch.run();
    }
}