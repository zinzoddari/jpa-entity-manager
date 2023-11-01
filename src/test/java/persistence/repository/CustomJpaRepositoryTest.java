package persistence.repository;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static persistence.sql.common.meta.MetaUtils.Columns을_생성함;
import static persistence.sql.common.meta.MetaUtils.TableName을_생성함;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import java.sql.SQLException;

import jdbc.JdbcTemplate;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityManagerFactory;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;
import persistence.sql.ddl.DmlQuery;

class CustomJpaRepositoryTest {
    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private static CustomJpaRepository<Person, Long> repository;

    @BeforeAll
    static void beforeAll() throws SQLException {
        server = new H2();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        repository = new CustomJpaRepository<>(EntityManagerFactory.of(server.getConnection()), Person.class);
        테이블을_생성함(Person.class);
    }

    @Test
    @DisplayName("저장에 성공")
    void save() {
        //given
        final Long id = 1L;
        final String name = "name";
        final Integer age = 30;
        final String email = "zz@cc.com";
        final Integer index = 1;

        Person person = new Person(id, name, age, email, index);

        //when
        repository.save(person);

        Person result = repository.findById(id);

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(result.getId()).isEqualTo(id);
            softAssertions.assertThat(result.getName()).isEqualTo(name);
            softAssertions.assertThat(result.getAge()).isEqualTo(age);
            softAssertions.assertThat(result.getEmail()).isEqualTo(email);
            softAssertions.assertThat(result.getIndex()).isNull();
        });
    }

    @Test
    @DisplayName("삭제에 성공")
    void remove() {
        //given
        final Long id = 1L;
        final String name = "name";
        final Integer age = 30;
        final String email = "zz@cc.com";
        final Integer index = 1;

        Person person = new Person(id, name, age, email, index);

        repository.save(person);

        //when
        repository.delete(id);

        Person result = repository.findById(id);

        //then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("수정에 성공")
    void update() {
        //given
        final Long id = 1L;
        final String name = "name";
        final Integer age = 30;
        final String email = "zz@cc.com";
        final Integer index = 1;

        Person person = new Person(id, name, age, email, index);
        person = repository.save(person);

        //when
        final String changeName = "홍길순";
        person.changeName(changeName);
        repository.commit();

        Person result = repository.findById(id);

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(result.getId()).isEqualTo(id);
            softAssertions.assertThat(result.getName()).isEqualTo(changeName);
            softAssertions.assertThat(result.getAge()).isEqualTo(age);
            softAssertions.assertThat(result.getEmail()).isEqualTo(email);
            softAssertions.assertThat(result.getIndex()).isNull();
        });
    }

    private static <T> void 테이블을_생성함(Class<T> tClass) {
        final TableName tableName = TableName을_생성함(tClass);
        final Columns columns = Columns을_생성함(tClass);

        jdbcTemplate.execute(DmlQuery.getInstance().create(tableName, columns));
    }
}