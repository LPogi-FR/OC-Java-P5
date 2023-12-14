package com.gpot.fr.safetynet.assembler;

import static com.gpot.fr.safetynet.utils.AppUtils.calculateAge;

import com.gpot.fr.safetynet.entity.MedicalRecords;
import com.gpot.fr.safetynet.entity.Person;
import com.gpot.fr.safetynet.model.*;
import com.gpot.fr.safetynet.utils.AppUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AlertAssembler {

  public List<CountAndAssembledList.FireStationNumberModel> toModelFindPersonsCoveredByStation(
    List<Person> personList
  ) {
    return personList.stream().map(this::toModelFindPersonsCoveredByStation).toList();
  }

  private CountAndAssembledList.FireStationNumberModel toModelFindPersonsCoveredByStation(Person person) {
    return CountAndAssembledList.FireStationNumberModel
      .builder()
      .firstName(person.getFirstName())
      .lastName(person.getLastName())
      .address(person.getAddress())
      .phone(person.getPhone())
      .build();
  }

  public List<InhabitantByAddressModel> toModelInhabitantByAddress(
    List<Person> personList,
    List<MedicalRecords> recordsList
  ) {
    final var inhabitantList = new ArrayList<InhabitantByAddressModel>();
    personList.forEach(person ->
      recordsList
        .stream()
        .filter(f ->
          f.getFirstName().equalsIgnoreCase(person.getFirstName()) &&
          f.getLastName().equalsIgnoreCase(person.getLastName())
        )
        .findFirst()
        .ifPresent(recordFound -> inhabitantList.add(this.toModelInhabitantByAddress(person, recordFound)))
    );
    return inhabitantList;
  }

  private InhabitantByAddressModel toModelInhabitantByAddress(Person person, MedicalRecords records) {
    return InhabitantByAddressModel
      .builder()
      .firstName(person.getFirstName())
      .lastName(person.getLastName())
      .phone(person.getPhone())
      .medication(records.getMedications())
      .allergies(records.getAllergies())
      .age(calculateAge(records))
      .build();
  }

  public List<HomeByStationListModel> toModelFindHomeByStationList(
    List<String> addressList,
    List<Person> personList,
    List<MedicalRecords> medicalRecords
  ) {
    final var homeList = new ArrayList<HomeByStationListModel>();
    addressList.forEach(address -> {
      final var listOfPerson = personList
        .stream()
        .filter(person -> person.getAddress().equalsIgnoreCase(address))
        .toList();
      listOfPerson.forEach(person ->
        medicalRecords
          .stream()
          .filter(m -> m.getFirstName().equalsIgnoreCase(person.getFirstName()))
          .findFirst()
          .ifPresent(m -> homeList.add(this.toModelFindHomeByStationList(person, m)))
      );
    });
    return homeList;
  }

  private HomeByStationListModel toModelFindHomeByStationList(Person person, MedicalRecords records) {
    return HomeByStationListModel
      .builder()
      .firstName(person.getFirstName())
      .lastName(person.getLastName())
      .address(person.getAddress())
      .phone(person.getPhone())
      .age(calculateAge(records))
      .allergies(records.getAllergies())
      .medication(records.getMedications())
      .build();
  }

  public List<PersonInfoModel> toModelPersonInfo(List<Person> personList, List<MedicalRecords> reccordList) {
    final var infoList = new ArrayList<PersonInfoModel>();
    personList.forEach(person ->
      reccordList.forEach(medicalRecord -> infoList.add(this.toModelPersonInfo(person, medicalRecord)))
    );
    return infoList;
  }

  private PersonInfoModel toModelPersonInfo(Person person, MedicalRecords records) {
    return PersonInfoModel
      .builder()
      .firstName(person.getFirstName())
      .lastName(person.getLastName())
      .address(person.getAddress())
      .email(person.getEmail())
      .age(calculateAge(records))
      .allergies(records.getAllergies())
      .medication(records.getMedications())
      .build();
  }

  public List<ChildAndFamilyModel> toModelFindChildByAddress(
    List<Person> personList,
    List<MedicalRecords> recordsList
  ) {
    final var childList = recordsList.stream().filter(AppUtils::isThereMinor).toList();
    if (childList.isEmpty()) {
      return new ArrayList<>();
    } else {
      return childList
        .stream()
        .map(records ->
          personList
            .stream()
            .filter(person ->
              person.getFirstName().equalsIgnoreCase(records.getFirstName()) &&
              person.getLastName().equalsIgnoreCase(records.getLastName())
            )
            .findFirst()
            .map(person ->
              ChildAndFamilyModel
                .builder()
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .age(calculateAge(records))
                .otherFamilyMember(buildOtherfamilyMember(personList, person))
                .build()
            )
            .orElse(null)
        )
        .toList();
    }
  }

  private List<ChildAndFamilyModel.PersonModel> buildOtherfamilyMember(List<Person> personList, Person person) {
    final var restOfFamily = new ArrayList<>(personList);
    restOfFamily.removeIf(p ->
      p.getLastName().equalsIgnoreCase(person.getLastName()) && p.getFirstName().equalsIgnoreCase(person.getFirstName())
    );
    return restOfFamily
      .stream()
      .map(p -> ChildAndFamilyModel.PersonModel.builder().firstName(p.getFirstName()).lastName(p.getLastName()).build())
      .toList();
  }

  public CountAndAssembledList toModelCountAndAssembledList(
    int minorCount,
    int majorCount,
    List<CountAndAssembledList.FireStationNumberModel> assembledList
  ) {
    return CountAndAssembledList.builder().majorCount(majorCount).minorCount(minorCount).persons(assembledList).build();
  }

  public StationNumberAndAssembledList toModelStationNumberAndAssembledList(
    String stationNumber,
    List<InhabitantByAddressModel> inhabitantList
  ) {
    return StationNumberAndAssembledList.builder().stationNumber(stationNumber).inhabitantList(inhabitantList).build();
  }
}
