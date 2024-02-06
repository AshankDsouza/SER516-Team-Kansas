package org.example.JavaTaigaCode.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class HelloWorldController
{
    @RequestMapping("/")
    public String hello()
    {
        return "Hello Pratima !! Good Morning";
    }
}
