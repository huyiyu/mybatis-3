/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

/**
 * 该类用于读取配置文件创建出一个SqlSessionFactory 对象,
 * build 有两种实现是因为 xmlConfigBuilder 既可以读取字符流也可以读取字节流
 * 本类仅关注两个Build方法
 */
public class SqlSessionFactoryBuilder {

  public SqlSessionFactory build(Reader reader) {
    return build(reader, null, null);
  }

  public SqlSessionFactory build(Reader reader, String environment) {
    return build(reader, environment, null);
  }

  public SqlSessionFactory build(Reader reader, Properties properties) {
    return build(reader, null, properties);
  }

  /**
   * mybatis SqlSessionFactory创建唯二核心方法,
   * @param reader configuration.xml 对象的字符流,xpath 通过获取reader 解析XML
   * @param environment environment 是对应的 environments 中的多套环境的含义
   * @param properties 可通过properties 配置Mybatis 的一些属性,无需使用默认的configuration.xml 相互替代关系
   * @return
   */
  public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
    try {
      // xmlConfigBuilder 是Mybatis 主配置文件的解析对象,所以这里获得一个reader,
      // 作为入参,environment 和properties 不是必传的,
      XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);
      return build(parser.parse());
    } catch (Exception e) {
      // 当出现错误时会从ErrorContext 中取出错误消息展示
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      // errorConext 作用是记录初始化过程中的错误消息在异常抛出的时候能打印最具体的内容方便定位
      // 内部是ThreadLocal对象,在启动过程中mybatis 肯定是单线程活动的,所以我们能很好定位错误
      ErrorContext.instance().reset();
      try {
        reader.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }

  public SqlSessionFactory build(InputStream inputStream) {
    return build(inputStream, null, null);
  }

  public SqlSessionFactory build(InputStream inputStream, String environment) {
    return build(inputStream, environment, null);
  }

  public SqlSessionFactory build(InputStream inputStream, Properties properties) {
    return build(inputStream, null, properties);
  }

  public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
    try {
      XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
      return build(parser.parse());
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        inputStream.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }

  public SqlSessionFactory build(Configuration config) {
    return new DefaultSqlSessionFactory(config);
  }

}
