package com.hnust;

import com.hnust.service.LikeService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 测试的四个步骤：
 * 1、初始化数据
 * 2、执行要测试的业务
 * 3、验证测试的数据
 * 4、清理数据
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class LikeServiceTests {
    @Autowired
    LikeService likeService;

    @Test
    public void testLike() {   //2、执行要测试的业务
        likeService.like(123, 1, 1);
        Assert.assertEquals(1, likeService.getLikeStatus(123,1,1));
    }

//    @Test
//    public void testDislike() {   //2、执行要测试的业务
//        likeService.disLike(1234, 1, 1);
//        Assert.assertEquals(1, likeService.getLikeStatus(1234,1,1));
//    }

    @Before
    public void setUp() {   //1、初始化数据
        System.out.println("setUp");
    }

    @After
    public void tearDown(){     //4、清理数据
        System.out.println("tearDown");
    }

    @BeforeClass
    public static void beforeClass() {
        System.out.println("beforeClass");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("afterClass");
    }
}
