package com.vesystem.version;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * @auther hcy
 * @create 2020-07-08 18:33
 * @Description
 */
public class CustomGenerator {
    public static void main(String[] args) {
        idpCode();
    }

    //生成idp的默认代码
    private static void idpCode(){
        String[] tables = new String[] { "doc_lock"};
        String[] models = new String[] { "module"};
        if ( tables.length !=models.length ){
            throw new RuntimeException("模块名和标明必须一一对应");
        }
        for (int i=0;i<tables.length;i++){
            mybatisPlusCodeUtil(models[i],tables[i]);
        }
    }

    private static void mybatisPlusCodeUtil(String modelName,String tableName){
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir("E:\\mybatisplustest");
//        gc.setFileOverride(true);
//        gc.setActiveRecord(true);
//        gc.setEnableCache(true);// XML 二级缓存
//        gc.setBaseResultMap(true);// XML ResultMap
//        gc.setBaseColumnList(true);// XML columList
        gc.setOpen(false);
        // gc.setSwagger2(true); 实体属性 Swagger2 注解
        gc.setAuthor("hcy");
        gc.setDateType(DateType.ONLY_DATE);//配置时间策略，不使用Time类中的时间类型
        mpg.setGlobalConfig(gc);


        // 自定义文件命名，注意 %s 会自动填充表实体属性！
//        gc.setMapperName("%sDao");
//        gc.setXmlName("%sMapper");
//        gc.setServiceName("%sService");
//        gc.setServiceImplName("%sServiceImap");
//        gc.setControllerName("%sController");
//        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
//        dsc.setDbType(DbType.MYSQL);
//        /*dsc.setTypeConvert(new MySqlTypeConvert(){
//            // 自定义数据库表字段类型转换【可选】
//            @Override
//            public DbColumnType processTypeConvert(String fieldType) {
//                System.out.println("转换类型：" + fieldType);
//                return super.processTypeConvert(fieldType);
//            }
//        });*/

        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUrl("jdbc:mysql://localhost:3306/vedoc?useUnicode=true&amp;characterEncoding=UTF-8&amp;generateSimpleParameterMetadata=true");
        dsc.setUsername("root");
        dsc.setPassword("3.14");
        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        // strategy.setCapitalMode(true);// 全局大写命名 ORACLE 注意
        // strategy.setTablePrefix(new String[] { "tlog_", "tsys_" });// 此处可以修改为您的表前缀
        strategy.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setInclude(new String[] { tableName }); // 需要生成的表
        // strategy.setExclude(new String[]{"test"}); // 排除生成的表
        mpg.setStrategy(strategy);
        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.vesystem.version");
        pc.setModuleName(modelName);
        mpg.setPackageInfo(pc);
        // 执行生成
        mpg.execute();
    }

}
