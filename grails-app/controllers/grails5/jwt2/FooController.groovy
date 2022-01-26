package grails5.jwt2

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PathVariable

//import org.springframework.security.access.annotation.Secured

import org.springframework.web.bind.annotation.RequestBody

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric

public class FooController {

    private static final Logger logger = LoggerFactory.getLogger(FooController.class);

    def findOne(@PathVariable Long id) {
        return new Foo(Long.parseLong(randomNumeric(2)), randomAlphabetic(4));
    }

//    @Secured("hasAnyRole('Global Administrator')") // 2022-01-21 bjs: Couldn't quite get this working; see https://grails.github.io/grails-spring-security-core/4.0.x/index.html#securedAnnotations
//    @Secured(closure = {
//        true
//    }) // 2022-01-21 bjs: Couldn't quite get this working; see https://grails.github.io/grails-spring-security-core/4.0.x/index.html#securedAnnotations
    def findAll() {
        List<Foo> fooList = new ArrayList<Foo>();
        fooList.add(new Foo(Long.parseLong(randomNumeric(2)), randomAlphabetic(4)));
        fooList.add(new Foo(Long.parseLong(randomNumeric(2)), randomAlphabetic(4)));
        fooList.add(new Foo(Long.parseLong(randomNumeric(2)), randomAlphabetic(4)));
        render fooList.toString()
    }

//    @Secured(['SuperDuperAdmin'])
    def findSecret() {
        List<Foo> fooList = new ArrayList<Foo>();
        fooList.add(new Foo(Long.parseLong(randomNumeric(2)), "Top Secret!"));
        fooList.add(new Foo(Long.parseLong(randomNumeric(2)), randomAlphabetic(4)));
        fooList.add(new Foo(Long.parseLong(randomNumeric(2)), randomAlphabetic(4)));
        render fooList.toString()
    }

    def create(@RequestBody Foo newFoo) {
        logger.info("Foo created");
    }

}