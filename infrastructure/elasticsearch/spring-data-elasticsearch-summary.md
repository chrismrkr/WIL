# Spring Data Elastic Search

Spring Data Repository를 통해 Elastic Search Document 및 Index를 다루는 방법에 대해 정리함

(참고 자료: https://docs.spring.io/spring-data/elasticsearch/reference/repositories.html)

## Query Method
### 1. Method Name Query
Method Name을 Field와 Keyword로 조합하여 ElasticSearch Query로 자동으로 변환하는 시키는 방법

#### Example
```java
interface BookRepository extends Repository<Book, String> {
  List<Book> findByNameAndPrice(String name, Integer price);
}
```
위 쿼리는 아래의 ElasticSearch Query로 변환됨
```
{
    "query": {
        "bool" : {
            "must" : [
                { "query_string" : { "query" : "?", "fields" : [ "name" ] } },
                { "query_string" : { "query" : "?", "fields" : [ "price" ] } }
            ]
        }
    }
}
```
#### Keyword
- And, Or, LessThen, LessThenEqual, Like, StartingWith, EndingWith 등을 지원함
- 공식 Document 참고할 것
- https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/repositories/elasticsearch-repository-queries.html#elasticsearch.query-methods.criterions

### 2. @Query Annotation
@Query Annotation에 직접 ElasticSearch Query를 선언함
#### Example 1 
```java
interface BookRepository extends ElasticsearchRepository<Book, String> {
    @Query("{\"match\": {\"name\": {\"query\": \"?0\"}}}")
    Page<Book> findByName(String name,Pageable pageable);
}
```
위 쿼리는 아래의 ElasticSearch Query로 변환됨
```
{
  "query": {
    "match": {
      "name": {
        "query": "John"
      }
    }
  }
}
```
#### Example 2
```java
@Query("{\"ids\": {\"values\": ?0 }}")
List<SampleEntity> getByIds(Collection<String> ids);
```
Collection 타입의 매개변수도 가능하며, 위 쿼리는 아래의 ElasticSearch Query로 변환됨
```
{
  "query": {
    "ids": {
      "values": ["id1", "id2", "id3"]
    }
  }
}
```
