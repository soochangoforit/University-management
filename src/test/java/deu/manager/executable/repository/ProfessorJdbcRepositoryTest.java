package deu.manager.executable.repository;


import com.jayway.jsonpath.internal.path.PredicatePathToken;
import deu.manager.executable.config.exception.DbInsertWrongParamException;
import deu.manager.executable.domain.Major;
import deu.manager.executable.domain.Professor;
import deu.manager.executable.domain.Student;
import deu.manager.executable.repository.interfaces.ProfessorRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.simpleflatmapper.map.property.OptionalProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@Sql(value = "professorTest_Init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ProfessorJdbcRepositoryTest {

    @Autowired
    ProfessorRepository repository;
    Logger logger = LogManager.getLogger(this.getClass());


    //Read
    @Test @DisplayName("find by id - single, success")
    public void findById_singleSuccess(){
       Optional<Professor> searched1 = repository.findById(1L);

        Professor target1 = Professor.builder()
                .id(1L)
                .name("교수이름1")
                .professorNum(1)
                .residentNum("1111111")
                .password("1111111")
                .lectures(null)
                .major(Major.builder().id(1L)
                        .name("학과1").build())
                .build();

        assertThat(searched1.isPresent()).isTrue();
        Professor actual1 = searched1.get();
        actual1.setLectures(null);
        assertThat(actual1).usingRecursiveComparison().isEqualTo(target1);


        Optional<Professor> searched2 = repository.findById(2L);

        Professor target2 = Professor.builder()
                .id(2L)
                .name("교수이름2")
                .professorNum(2)
                .residentNum("2222222")
                .password("2222222")
                .lectures(null)
                .major(Major.builder().id(2L)
                        .name("학과2").build())
                .build();

        assertThat(searched2.isPresent()).isTrue();
        Professor actual2 = searched2.get();
        actual2.setLectures(null);
        assertThat(actual2).usingRecursiveComparison().isEqualTo(target2);

    }


    @Test @DisplayName("find by id - single , wrong input")
    public void findById_singleFailed(){
        //search wrong id
        Optional<Professor> searched = repository.findById(20L);
        assertThat(searched.isPresent()).isFalse();

    }


    @Test @DisplayName("search by id - multiple")
    public void findById_multiple(){
        List<Long> ids = new ArrayList<>(Arrays.asList(1L, 2L));
        List<Professor> target = new ArrayList<>(Arrays.asList(
                Professor.builder()
                        .id(1L)
                        .name("교수이름1")
                        .professorNum(1)
                        .password("1111111")
                        .lectures(null)
                        .residentNum("1111111")
                        .major(Major.builder().id(1L).name("학과1").build()).
                        build(),


                Professor.builder()
                        .id(2L)
                        .name("교수이름2")
                        .professorNum(2)
                        .password("2222222")
                        .lectures(null)
                        .residentNum("2222222")
                        .major(Major.builder().id(2L).name("학과2").build()).
                        build()
        ));

        List<Professor> searched = repository.findById(ids);
        for(Professor professor : searched){
            professor.setLectures(null);
        }
        assertThat(searched.isEmpty()).isFalse();

        /*
        for(int i = 0 ; i < searched.size() ; i++){
            assertThat(searched.indexOf(i)).usingRecursiveComparison().isEqualTo(target.indexOf(i));
        }
        */
        assertThat(searched).usingRecursiveComparison().isEqualTo(target);

    }


    @Test @DisplayName("find by Professor Number success")
    public void findByProfessorNum_success(){
        Professor target = Professor.builder()
                    .id(1L)
                    .name("교수이름1")
                    .professorNum(1)
                    .password("1111111")
                    .lectures(null)
                    .residentNum("1111111")
                    .major(Major.builder().id(1L).name("학과1").build()).
                    build();

        Optional<Professor> searched = repository.findByProfessorNum(1);

        assertThat(searched.isPresent()).isTrue();

        Professor searched1 = searched.get();
        searched1.setLectures(null);

        assertThat(searched1).usingRecursiveComparison().isEqualTo(target);
    }



    @Test @DisplayName("find by name")
    public void findByName(){
        Professor target = Professor.builder()
                .id(2L)
                .name("교수이름2")
                .professorNum(2)
                .password("2222222")
                .lectures(null)
                .residentNum("2222222")
                .major(Major.builder().id(2L).name("학과2").build()).
                build();

        List<Professor> searchedList = repository.findByName("교수이름2");

        assertThat(searchedList.size()).isEqualTo(1);
        searchedList.stream().findAny().get().setLectures(null);
        assertThat(searchedList.get(0)).usingRecursiveComparison().isEqualTo(target);

    }

    //Save
    @Test @DisplayName("save - success")
    public void save_success() throws DbInsertWrongParamException{
        Professor saveObject = Professor.builder()
                .name("교수5")
                .professorNum(5)
                .password("5555555")
                .lectures(null)
                .residentNum("5555555")
                .major(Major.builder()
                        .id(1L)
                        .name("학과1").build())
                .build();

        Professor savedObject = repository.save(saveObject);
        Optional<Professor> searchedObject = repository.findById(savedObject.getId());

        assertThat(searchedObject.isPresent()).isTrue();
        Professor searchedObject1 = searchedObject.get();
        searchedObject1.setLectures(null);

        assertThat(savedObject).usingRecursiveComparison().isEqualTo(searchedObject1);
    }

    @Test @DisplayName("save - fail case")
    public void save_failure() throws DbInsertWrongParamException {

        //Check ID nullCheck Exception - ID should be null
        Professor idCheckDto = Professor.builder()
                .id(20L)
                .name("교수이름5")
                .professorNum(5)
                .password("5555555")
                .lectures(null)
                .residentNum("5555555")
                .major(Major.builder()
                        .id(2L)
                        .name("학과2").build())
                .build();

        assertThatThrownBy(() -> {
            repository.save(idCheckDto);
            logger.warn("Exception not thrown - Id Check");
        }).isInstanceOf(DbInsertWrongParamException.class);



        //Student Number Duplicate check
        Professor professorNumDupCheck = Professor.builder()
                .id(null)
                .name("교수5")
                .professorNum(1) // 사용자 넘버 중복 저장
                .password("5555555")
                .lectures(null)
                .residentNum("5555555")
                .major(Major.builder()
                        .id(2L)
                        .name("학과2").build())
                .build();

        assertThatThrownBy(() -> {
            repository.save(professorNumDupCheck);
            logger.warn("Exception not thrown - Professor Number Duplicate check");
        }).isInstanceOf(DataAccessException.class);


        //Invalid Major foreign key check
        Professor majorWrongKeyCheck = Professor.builder()
                .id(null)
                .name("교수이름5")
                .password("5555555")
                .residentNum("5555555")
                .professorNum(5)
                .major(Major.builder()
                        .id(120L)
                        .name("학과2").build())
                .lectures(null).build();

        assertThatThrownBy(() -> {
            repository.save(majorWrongKeyCheck);
            logger.warn("Exception not thrown - major wrong key check");
        }).isInstanceOf(DataAccessException.class);

    }

    //Update
    @Test
    @DisplayName("Update test - success")
    public void updateTest_success() throws DbInsertWrongParamException {

        {
            //ProfessorNum은 Update 되면 안되기 때문에 제외하였다.
            Professor fullUpdate = Professor.builder()
                    .id(1L)
                    .name("교수이름5")
                    .password("5555555")
                    .residentNum("5555555")
                    .major(Major.builder()
                            .id(2L)
                            .name("학과2")
                            .build()).build();

            repository.update(fullUpdate);

            Professor target = Professor.builder()
                    .id(1L)
                    .name("교수이름5")
                    .password("5555555")
                    .lectures(null)
                    .professorNum(1)
                    .residentNum("5555555")
                    .major(Major.builder()
                            .id(2L)
                            .name("학과2")
                            .build()).build();


            Optional<Professor> searched = repository.findById(1L);
            assertThat(searched.isPresent()).isTrue();
            Professor searched1 = searched.get();
            searched1.setLectures(null);

            assertThat(searched1).usingRecursiveComparison().isEqualTo(target);

        }

        {
            //Optional update ,위에서 Update된 데이터를 그대로 한번더 Update
            Professor optionalUpdate = Professor.builder()
                    .id(1L)
                    .name("교수이름6").build();

            repository.update(optionalUpdate);

            // 이름만 변경이 되고 , 나머지 정보는 Full-update에 의해서 생긴 데이터 와 동일하게 한뒤 , 검사 할때는 professorNum까지 저장해서 확인
            Professor target = Professor.builder()
                    .id(1L)
                    .name("교수이름6")
                    .password("5555555")
                    .professorNum(1)
                    .residentNum("5555555")
                    .lectures(null)
                    .major(Major.builder()
                            .id(2L)
                            .name("학과2")
                            .build()).build();


            Optional<Professor> searched = repository.findById(1L);
            assertThat(searched.isPresent()).isTrue();
            Professor searched1 = searched.get();
            searched1.setLectures(null);

            assertThat(searched1).usingRecursiveComparison().isEqualTo(target);

        }
    }



    @Test
    @DisplayName("update - failure")
    public void update_failure() throws DbInsertWrongParamException{
        //no ID case
        {
            Professor noId = Professor.builder()
                    .name("교수7")
                    .residentNum("1231231112221").build();

            assertThatThrownBy(() -> {
                repository.update(noId);
                logger.warn("No exception thrown - update with no id");
            }).isInstanceOf(DbInsertWrongParamException.class);
        }

        //Change Student number case
        {
            Professor changeProfessorNum = Professor.builder()
                    .id(1L)
                    .professorNum(10)
                    .name("교수8")
                    .residentNum("1231231112221").build();

            assertThatThrownBy(() -> {
                repository.update(changeProfessorNum);
                logger.warn("No exception thrown - update professor number");
            }).isInstanceOf(DbInsertWrongParamException.class);
        }

        //Change invalid major id foreign key case
        {
            Professor changeProfessorMajor = Professor.builder()
                    .id(1L)
                    .name("학생변경")
                    .major(Major.builder()
                            .id(10L)
                            .name("잘못된학과")
                            .build()).build();

            assertThatThrownBy(() -> {
                repository.update(changeProfessorMajor);
                logger.warn("No exception thrown - update student number");
            }).isInstanceOf(DataAccessException.class);
        }
    }


    //Delete
    @Test
    @DisplayName("delete - success")
    public void delete_success(){
        Optional<Professor> searchedBefore = repository.findById(2L);
        assertThat(searchedBefore.isPresent()).isTrue();

        repository.delete(2L);
        Optional<Professor> searchedAfter = repository.findById(2L);

        assertThat(searchedAfter.isPresent()).isFalse();
    }

    @Test
    @DisplayName("delete - multiple")
    public void delete_multiple() {
        List<Professor> searchedBefore = repository.findById(new ArrayList<>(Arrays.asList(1L, 2L)));
        assertThat(searchedBefore).hasSize(2);

        repository.delete(new ArrayList<>(Arrays.asList(1L, 2L)));
        List<Professor> searched = repository.findById(new ArrayList<>(Arrays.asList(1L, 2L)));
        assertThat(searched.isEmpty()).isTrue();
    }


}
