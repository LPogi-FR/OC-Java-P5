package com.gpot.fr.safetynet.repository.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gpot.fr.safetynet.entity.FireStation;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FireStationRepositoryImpTest {

  private FireStationRepositoryImp repository;

  @BeforeEach
  void beforeEach() throws IOException {
    repository = new FireStationRepositoryImp();

    DataRepository.init();
    assertEquals(13, FireStationRepositoryImp.FIRE_STATION_LIST.size());
  }

  @AfterEach
  public void afterEach() {
    repository = null;
  }

  @Test
  void itShouldSave() {
    repository.save(FireStation.builder().build());
    assertEquals(14, FireStationRepositoryImp.FIRE_STATION_LIST.size());
  }

  @Test
  void itShouldDelete() {
    repository.delete("1509 Culver St");
    assertEquals(12, FireStationRepositoryImp.FIRE_STATION_LIST.size());
  }

  @Test
  void itShouldFindAll() {
    final var result = repository.findAll();
    assertEquals(13, result.size());
  }

  @Test
  void itShouldFindAddressByStationNumber() {
    final var result = repository.findAddressByStationNumber("3");
    assertEquals(5, result.size());
  }

  @Test
  void itShouldFindStationNumberByAddress() {
    final var result = repository.findStationNumberByAddress("1509 Culver St");
    assertEquals("3", result);
  }

  @Test
  void itShouldUpdate() {
    assertEquals("3", FireStationRepositoryImp.FIRE_STATION_LIST.get(0).getStation());
    repository.update(FireStation.builder().address("1509 Culver St").station("10").build());
    assertEquals("10", FireStationRepositoryImp.FIRE_STATION_LIST.get(0).getStation());
  }
}
