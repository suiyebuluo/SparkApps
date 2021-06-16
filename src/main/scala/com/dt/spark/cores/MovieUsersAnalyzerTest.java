package com.dt.spark.cores;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.List;

/**
 * @title: 二次排序测试类
 * @description: 测试SecondarySortingKey.java的功能排序
 * @author: jguo
 * @date: 2021/6/16
 */
public class MovieUsersAnalyzerTest {
    public static void main(String[] args) {
        //设置打印日志的输出级别
        Logger.getLogger("org").setLevel(Level.WARN);
        /**
         * 创建Spark集群上下文sc，在sc上进行各种依赖和参数的设置。
         */
        JavaSparkContext sc = new JavaSparkContext(new SparkConf().setMaster("local[4]").setAppName("Movie_Users_Analyzer"));
        JavaRDD<String> lines = sc.textFile("data/" + "dataforsecondarysorting.txt");
        JavaPairRDD<SecondarySortingKey,String> keyValues = lines.mapToPair(new PairFunction<String, SecondarySortingKey, String>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Tuple2<SecondarySortingKey, String> call(String s) throws Exception {
                String[] splited = s.split(" ");
                SecondarySortingKey key = new SecondarySortingKey(Integer.valueOf(splited[0]),Integer.valueOf(splited[1])); //组合成key值
                return new Tuple2<SecondarySortingKey,String>(key,s);
            }
        });
        //按key值进行二次排序
        JavaPairRDD<SecondarySortingKey,String> sorted = keyValues.sortByKey(false);
        JavaRDD<String> result = sorted.map(new Function<Tuple2<SecondarySortingKey, String>, String>() {
            private static final long serialVersionUID = 1L;
            @Override
            public String call(Tuple2<SecondarySortingKey, String> tuple) throws Exception {
                return tuple._2;//取第二个value值
            }
        });
        List<String> collected = result.take(10);
        for (String item:collected) {   //输出二次排序后的结果
            System.out.println(item);
        }
    }

}
