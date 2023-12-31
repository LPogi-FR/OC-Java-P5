package com.gpot.fr.safetynet.controller;

import static com.gpot.fr.safetynet.utils.AppUtils.*;

import com.gpot.fr.safetynet.assembler.AlertAssembler;
import com.gpot.fr.safetynet.entity.MedicalRecords;
import com.gpot.fr.safetynet.entity.Person;
import com.gpot.fr.safetynet.model.*;
import com.gpot.fr.safetynet.service.FireStationService;
import com.gpot.fr.safetynet.service.MedicalRecordsService;
import com.gpot.fr.safetynet.service.PersonService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
public class AlertController {

  private final MedicalRecordsService medicalRecordsService;
  private final FireStationService fireStationService;
  private final PersonService personService;
  private final AlertAssembler alertAssembler;

  @GetMapping("/firestation")
  public ResponseEntity<CountAndAssembledList> findPersonsCoveredByStation(
    @RequestParam(name = "stationNumber") String stationNumber
  ) {
    log.info("User call to method findPersonCoveredByStation");
    setMajorCount(0);
    setMinorCount(0);
    List<String> addressList = fireStationService.findAddressByStationNumber(stationNumber);
    List<Person> personList = personService.findPersonByAddressList(addressList);
    List<MedicalRecords> recordsList = medicalRecordsService.findRecordsByPersonList(personList);
    List<CountAndAssembledList.FireStationNumberModel> assembledList =
      alertAssembler.toModelFindPersonsCoveredByStation(personList);
    for (MedicalRecords records : recordsList) {
      if (isThereMinor(records)) {
        setMinorCount(getMinorCount() + 1);
      } else {
        setMajorCount(getMajorCount() + 1);
      }
    }
    CountAndAssembledList countAndAssembledList = alertAssembler.toModelCountAndAssembledList(
      getMinorCount(),
      getMajorCount(),
      assembledList
    );
    log.info(asJson(countAndAssembledList));
    return new ResponseEntity<>(countAndAssembledList, HttpStatus.OK);
  }

  @GetMapping("/childAlert")
  public ResponseEntity<List<ChildAndFamilyModel>> findChildByAddress(@RequestParam(name = "address") String address) {
    log.info("User call to method findChildByAddress");
    List<Person> personList = personService.findPersonByAddress(address);
    List<MedicalRecords> recordsList = medicalRecordsService.findRecordsByPersonList(personList);
    List<ChildAndFamilyModel> childList = alertAssembler.toModelFindChildByAddress(personList, recordsList);
    childList.sort(Comparator.comparing(ChildAndFamilyModel::getAge));
    log.info(asJson(childList));
    return new ResponseEntity<>(childList, HttpStatus.OK);
  }

  @GetMapping("/phoneAlert")
  public ResponseEntity<List<String>> findPhoneByStation(@RequestParam(name = "firestation") String stationNumber) {
    log.info("User call to method findPhoneByStation");
    List<String> addressList = fireStationService.findAddressByStationNumber(stationNumber);
    List<String> phoneList = personService.findPhoneByStationList(addressList);
    log.info(asJson(phoneList));
    return new ResponseEntity<>(phoneList, HttpStatus.OK);
  }

  @GetMapping("/fire")
  public ResponseEntity<StationNumberAndAssembledList> findInhabitantByAddress(
    @RequestParam(name = "address") String address
  ) {
    log.info("User call to method findInhabitantByAddress");
    String stationNumber = fireStationService.findStationNumberByAddress(address);
    List<Person> personList = personService.findPersonByAddress(address);
    List<MedicalRecords> recordsList = medicalRecordsService.findRecordsByPersonList(personList);
    List<InhabitantByAddressModel> inhabitantList = alertAssembler.toModelInhabitantByAddress(personList, recordsList);
    StationNumberAndAssembledList stationNumberAndAssembledList = alertAssembler.toModelStationNumberAndAssembledList(
      stationNumber,
      inhabitantList
    );
    log.info(asJson(stationNumberAndAssembledList));
    return new ResponseEntity<>(stationNumberAndAssembledList, HttpStatus.OK);
  }

  @GetMapping("/flood/stations")
  public ResponseEntity<List<HomeByStationListModel>> findHomeByStationList(
    @RequestParam(name = "stations") List<String> stationList
  ) {
    log.info("User call to method findHomeByStationList");
    List<String> addressList = new ArrayList<>();
    for (String stationNumber : stationList) {
      addressList.addAll(fireStationService.findAddressByStationNumber(stationNumber));
    }
    List<Person> personList = personService.findPersonByAddressList(addressList);
    List<MedicalRecords> recordsList = medicalRecordsService.findRecordsByPersonList(personList);
    List<HomeByStationListModel> assembledHomeList = alertAssembler.toModelFindHomeByStationList(
      addressList,
      personList,
      recordsList
    );
    log.info(asJson(assembledHomeList));
    return new ResponseEntity<>(assembledHomeList, HttpStatus.OK);
  }

  @GetMapping("/personInfo")
  public ResponseEntity<List<PersonInfoModel>> findPersonInfoByName(
    @RequestParam(name = "firstName") String firstName,
    @RequestParam(name = "lastName") String lastName
  ) {
    log.info("User call to method findPersonInfoByName");
    List<Person> personList = personService.findPersonsByFirstAndLastName(firstName, lastName);
    List<MedicalRecords> reccordList = medicalRecordsService.findRecordsByPersonList(personList);
    List<PersonInfoModel> assembledPersonInfo = alertAssembler.toModelPersonInfo(personList, reccordList);
    log.info(asJson(assembledPersonInfo));
    return new ResponseEntity<>(assembledPersonInfo, HttpStatus.OK);
  }

  @GetMapping("/communityEmail")
  public ResponseEntity<List<String>> findEmailByCity(@RequestParam(name = "city") String city) {
    log.info("User call to method findEmailByCity");
    List<String> emailList = personService.findEmailByCity(city);
    log.info(asJson(emailList));
    return new ResponseEntity<>(emailList, HttpStatus.OK);
  }
}
