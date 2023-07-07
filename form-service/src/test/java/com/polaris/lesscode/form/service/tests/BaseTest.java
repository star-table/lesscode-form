package com.polaris.lesscode.form.service.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
/**
 * @Author: Liu.B.J
 * @Data: 2020/9/1 15:42
 * @Modified:
 */

import com.polaris.lesscode.form.FormApplication;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = {FormApplication.class})
@AutoConfigureMockMvc
public class BaseTest {

    @BeforeClass
    public static void beforeClass() {

    }


    @AfterClass
    public static void afterClass() {

    }
}
