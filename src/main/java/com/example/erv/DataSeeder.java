package com.example.erv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.erv.config.AppConfig;
import com.example.erv.model.Card;
import com.example.erv.model.DepartmentUnit;
import com.example.erv.model.Device;
import com.example.erv.model.Location;
import com.example.erv.model.Permission;
import com.example.erv.model.Role;
import com.example.erv.model.Shift;
import com.example.erv.model.Timetable;
import com.example.erv.model.User;
import com.example.erv.model.enums.ActionType;
import com.example.erv.model.enums.CardType;
import com.example.erv.model.enums.UserType;
import com.example.erv.service.card.CardService;
import com.example.erv.service.departmentunit.DepartmentUnitService;
import com.example.erv.service.device.DeviceService;
import com.example.erv.service.location.LocationService;
import com.example.erv.service.permission.PermissionService;
import com.example.erv.service.role.RoleService;
import com.example.erv.service.shift.ShiftService;
import com.example.erv.service.timetable.TimetableService;
import com.example.erv.service.user.UserService;
import com.example.erv.utils.AppUtil;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserService userService;
    private final PermissionService permissionService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final DeviceService deviceService;
    private final DepartmentUnitService departmentUnitService;
    private final LocationService locationService;
    private final CardService cardService;
    private final TimetableService timetableService;
    private final ShiftService shiftService;
    private static final Logger logger = LogManager.getLogger(DataSeeder.class);

    @Value("${app.seed-demo-data:false}")
    private boolean seedDemoData;

    @Value("${app.seed-demo-timetables:false}")
    private boolean seedDemoTimetables;

    public DataSeeder(UserService userService, PermissionService permissionService, RoleService roleService, PasswordEncoder passwordEncoder, DeviceService deviceService,
                      DepartmentUnitService departmentUnitService, LocationService locationService, CardService cardService, TimetableService timetableService, ShiftService shiftService) {
        super();
        this.shiftService = shiftService;
        this.userService = userService;
        this.permissionService = permissionService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.deviceService = deviceService;
        this.departmentUnitService = departmentUnitService;
        this.locationService = locationService;
        this.cardService = cardService;
        this.timetableService = timetableService;
    }

    public static void main(String[] args) {
	}
    @Override
    public void run(String... args) throws Exception {
        if (seedDemoData && userService.findAll().isEmpty()) {
            generateData();
        }
    }
    
    private void generateData() throws Exception {
    	logger.info("Loading data...");
        User user = new User();
        user.setUsername("admin");
        String encodedPassword = this.passwordEncoder.encode("admin");
        user.setPassword(encodedPassword);
        user.setUserType(UserType.ADMIN);
        user.setFirstName("First");
        user.setLastName("last");

        Permission permission = new Permission();
        permission.setName("super_user");
        permissionService.save(permission);
        Permission permission2 = new Permission();
        permission2.setName("admin2");
        permissionService.save(permission2);
        createPermissions();
        Role role = new Role();
        role.setName("SuperUser");
        role.setPermissionList(List.of(permission));
        roleService.save(role);
        user.setRole(role);
        createDepartmentUnits();
        List<Shift> shifts = createShifts();
        List<User> users = createUsers(shifts);
        Location location = createLocation("Test lokacija", "Poslovna 15");
        createDevices(location);
        createDepartmentUnits();
        createLocations();
        createDevices(); 
        if (seedDemoTimetables) {
            createTimetables(users);
        }
        user.setDepartmentUnit(departmentUnitService.findById(1L).get());
        userService.save(user);
        logger.info("Data loaded successfully!");
        String cards = "";
        for(Card card : cardService.findAll()) {
        	cards += card.getRfid() + ", ";
        }
        logger.info(cards);
    }
    
    

    private void createTimetables(List<User> users) {
        List<Device> devices = deviceService.findAll();
        int pausePercent = 40;
        int businessPercent = 20;
        int businessNoReturnPercent = 50;

        List<Thread> threads = Arrays.asList(
//            createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2021-01-01", "2021-12-31"),
//            createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2022-01-01", "2022-12-31"),
//            createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2023-01-01", "2023-12-31"),
//            createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2024-01-01", LocalDate.now().toString())
//        		
//        		createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2021-01-01", "2021-06-30"),
//        		createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2021-07-01", "2021-12-31"),
//                createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2022-01-01", "2022-06-30"),
//                createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2022-07-01", "2022-12-31"),
//                createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2023-01-01", "2023-06-30"),
//                createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2023-07-01", "2023-12-31"),
//                createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2024-01-01", "2024-06-30"),
//                createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2024-07-01", "2024-12-31"),
//                createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2025-01-01", "2025-06-30")
        		createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2024-01-01", "2024-02-28"),
        		createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2024-03-01", "2024-04-30"),
        		createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2024-05-01", "2024-06-30"),
                createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2024-07-01", "2024-08-31"),
                createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2024-09-01", "2024-10-31"),
                createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2024-11-01", "2024-12-31"),
        		createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2025-01-01", "2025-02-28"),
        		createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2025-03-01", "2025-04-30"),
        		createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2025-05-01", "2025-06-30")
//                createTimetableThread(users, devices, pausePercent, businessPercent, businessNoReturnPercent, "2025-07-01", "2025-12-31")
        );

        threads.forEach(Thread::start);
    }

    private Thread createTimetableThread(List<User> users, List<Device> devices, int pausePercent, int businessPercent, int businessNoReturnPercent, String startDate, String endDate) {
        return new Thread(() -> {
        	logger.info(Thread.currentThread().getName() + " Creating timetable data..!");
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            LocalDate current = start;
            while (!current.isAfter(end)) {
                for (User user : DataSeederUtils.getEightyPercentOfObjects(users)) {
                    boolean wasOnPause = DataSeederUtils.returnTrueForPercentOfTime(pausePercent);
                    boolean wasOnBusinessTrip = DataSeederUtils.returnTrueForPercentOfTime(businessPercent);
                    boolean wasOnBusinessTripWithoutReturn = DataSeederUtils.returnTrueForPercentOfTime(businessNoReturnPercent);
                    createTimetable(user, current, DataSeederUtils.getRandomObject(devices), wasOnPause, wasOnBusinessTrip, wasOnBusinessTripWithoutReturn);
                }
                current = current.plusDays(1);
            }
        	logger.info(Thread.currentThread().getName() + " Timetable data created successfully from " + startDate + "to" + endDate + "!");
        });
    }

    public List<Shift> createShifts() {
        List<Shift> shifts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            // Create a new Shift object
            Shift shift = new Shift();
            shift.setStartFrom(String.format("%02d", 7 + i) + ":00");
            shift.setStartTo(String.format("%02d", 9 + i) + ":00");
            shift.setEndFrom(String.format("%02d", 15 + i) + ":00");
            shift.setEndTo(String.format("%02d", 17 + i) + ":00");
            shift.setDurationHours(8);
            shift.setDurationMinutes(0);
            shifts.add(shiftService.save(shift));
        }
        return shifts;
    }

    private void createTimetable(User user, LocalDate date, Device device, boolean wasOnPause, boolean wasOnBussinesTrip, boolean returnedFromBusinessTrip) {
    	Shift shift = DataSeederUtils.getRandomObject(new ArrayList<>(user.getShifts()));
        Timetable timetable = new Timetable();
        timetable.setActionType(ActionType.CHECK_IN);
        timetable.setDateTime(DataSeederUtils.getRandomDateTimeInRange(date, Integer.parseInt(shift.getStartFrom().split(":")[0]), 30));
        timetable.setUser(user);
        timetable.setShift(shift);
        timetable.setDevice(device);
        timetableService.save(timetable);
        
        if(returnedFromBusinessTrip || !wasOnBussinesTrip) {
	        Timetable t2 = new Timetable();
	        t2.setDateTime(DataSeederUtils.getRandomDateTimeInRange(date, timetable.getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())).getHour() + shift.getDurationHours(), 30));
	        t2.setUser(user);
	        t2.setShift(shift);
	        t2.setDevice(device);
	        t2.setActionType(ActionType.CHECK_OUT);
	        t2.setGroupingKey(timetable.getId());
	        timetableService.save(t2);
        }

        if(wasOnPause) {
        	Timetable pauseStart = new Timetable();
        	LocalDateTime pauseStartTime = LocalDateTime.of(date, LocalTime.of(timetable.getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())).getHour() + (DataSeederUtils.getRandomNumberBetween(1, shift.getDurationHours()-1)),DataSeederUtils.getRandomNumberBetween(0, 30)));
        	pauseStart.setActionType(ActionType.PAUSE_START);
        	pauseStart.setDateTime(ZonedDateTime.of(pauseStartTime, ZoneId.of(AppConfig.getTimezoneId())).toInstant());
        	pauseStart.setUser(user);
        	pauseStart.setShift(shift);
        	pauseStart.setDevice(device);
        	pauseStart.setGroupingKey(timetable.getId());
        	timetableService.save(pauseStart);
        	
        	Timetable pauseEnd = new Timetable();
        	pauseEnd.setActionType(ActionType.PAUSE_END);
        	pauseEnd.setDateTime(ZonedDateTime.of(pauseStartTime.plusMinutes(DataSeederUtils.getRandomNumberBetween(0, 30)), ZoneId.of(AppConfig.getTimezoneId())).toInstant());
        	pauseEnd.setUser(user);
        	pauseEnd.setShift(shift);
        	pauseEnd.setDevice(device);
        	pauseEnd.setGroupingKey(timetable.getId());
        	timetableService.save(pauseEnd);
        }
        
        if(wasOnBussinesTrip) {
        	LocalDateTime bussinesOutTime = LocalDateTime.of(date, LocalTime.of(timetable.getDateTime().atZone(ZoneId.of(AppConfig.getTimezoneId())).getHour() + (DataSeederUtils.getRandomNumberBetween(1, shift.getDurationHours()-1)),DataSeederUtils.getRandomNumberBetween(0, 30)));
        	Timetable bussinessOut = new Timetable();
        	bussinessOut.setActionType(ActionType.BUSINESS_START);
        	bussinessOut.setDateTime(ZonedDateTime.of(bussinesOutTime, ZoneId.of(AppConfig.getTimezoneId())).toInstant());
        	bussinessOut.setUser(user);
        	bussinessOut.setShift(shift);
        	bussinessOut.setDevice(device);
        	bussinessOut.setGroupingKey(timetable.getId());
        	timetableService.save(bussinessOut);
        	if(returnedFromBusinessTrip) {
        		Timetable bussinessIn = new Timetable();
        		bussinessIn.setActionType(ActionType.BUSINESS_END);
        		bussinessIn.setDateTime(ZonedDateTime.of(bussinesOutTime.plusMinutes(DataSeederUtils.getRandomNumberBetween(0, 300)), ZoneId.of(AppConfig.getTimezoneId())).toInstant());
        		bussinessIn.setUser(user);
        		bussinessIn.setShift(shift);
        		bussinessIn.setDevice(device);
        		bussinessIn.setGroupingKey(timetable.getId());
        		timetableService.save(bussinessIn);        		
        	}
        }
    }

    private void createDepartmentUnits() {
        createDepartmentUnit("EXAMPLE", null);
        createDepartmentUnit("SAMOSTALNE ORGANIZACIONE JEDINICE", 1L);
        createDepartmentUnit("SLUŽBA INTERNE REVIZIJE", 2L);
        createDepartmentUnit("SLUŽBA UPRAVLJANJA KVALITETOM", 2L);
        createDepartmentUnit("SLUŽBA UPRAVLJANJA INFORMACIONIM SISTEMOM", 2L);
        createDepartmentUnit("SLUŽBA BEZBEDNOSTI I ZDRAVLJA NA RADU", 2L);
        createDepartmentUnit("ODELJENJE KOMUNIKACIJA", 2L);
        createDepartmentUnit("SLUŽBA UPRAVLJANJA PROJEKTIMA", 2L);
        createDepartmentUnit("POGON 1", 1L);
        createDepartmentUnit("SEKTOR OPERATIVE", 1L); // ******************** 10
        createDepartmentUnit("SLUŽBA KONTROLE KVALITETA USLUGA", 10L);
        createDepartmentUnit("POGON 2", 1L);
        createDepartmentUnit("POGON 3", 1L);
        createDepartmentUnit("POGON 4", 1L);
        createDepartmentUnit("INFO CENTAR", 2L);
        createDepartmentUnit("POGON 5", 1L);
        createDepartmentUnit("SEKTOR ODRŽAVANJA", 1L);
        createDepartmentUnit("ODELJENJE KOMPJUTERSKE DIJAGNOSTIKE", 17L);
        createDepartmentUnit("POGON 6", 1L);
        createDepartmentUnit("POGON 7", 1L); // ******************** 20
        createDepartmentUnit("POGON 8", 1L);
        createDepartmentUnit("POGON 9", 1L);
        createDepartmentUnit("POGON 10", 1L);
        createDepartmentUnit("POGON 11", 1L);
        createDepartmentUnit("SLUŽBA PODRŠKE VOZNOG PARKA", 10L);
        createDepartmentUnit("SLUŽBA PROJEKTOVANJA USLUGA I NORMIRANJA", 10L);
        createDepartmentUnit("ODELJENJE EKSPLOATACIJE", 17L);
        createDepartmentUnit("ODELJENJE NORMIRANJA", 10L);
        createDepartmentUnit("ODELJENJE PRIBAVLJANJA DOZVOLA", 10L);
        createDepartmentUnit("SLUŽBA NADZORA I OBRADE PODATAKA", 2L); // ******************** 30
        createDepartmentUnit("ODELJENJE REKLAMACIJA", 10L);
        createDepartmentUnit("SEKTOR LOGISTIKE", 1L);
        createDepartmentUnit("SLUŽBA TRANSPORTA", 32L);
        createDepartmentUnit("POGON 12", 32L);
        createDepartmentUnit("SEKTOR PRAVNIH, KADROVSKIH I OPŠTIH POSLOVA", 1L);
        createDepartmentUnit("SLUŽBA PRAVNIH POSLOVA", 35L);
        createDepartmentUnit("SLUŽBA KADROVSKIH POSLOVA", 35L);
        createDepartmentUnit("SLUŽBA PROTIVPOŽARNE ZAŠTITE", 35L);
        createDepartmentUnit("SLUŽBA OPŠTIH POSLOVA", 35L);
        createDepartmentUnit("ODELJENJE DRUŠTVENOG STANDARDA", 35L); // ******************** 40
        createDepartmentUnit("SEKTOR EKONOMSKIH POSLOVA", 1L);
        createDepartmentUnit("SLUŽBA KNJIGOVODSTVENIH POSLOVA", 41L);
        createDepartmentUnit("SLUŽBA FINANSIJSKIH POSLOVA", 41L);
        createDepartmentUnit("SLUŽBA PRIHODA", 41L);
        createDepartmentUnit("SLUŽBA PLANA, ANALIZE I RAZVOJA", 41L);
        createDepartmentUnit("SEKTOR KOMERCIJALNIH POSLOVA", 1L);
        createDepartmentUnit("SLUŽBA JAVNIH NABAVKI", 46L);
        createDepartmentUnit("SLUŽBA KOMERCIJALNIH POSLOVA", 46L);
        createDepartmentUnit("ODELJENJE NABAVKE AUTO-DELOVA I USLUGA", 46L);
        createDepartmentUnit("MAGACINSKA SLUŽBA", 46L); // ******************** 50
        createDepartmentUnit("SLUŽBA OSIGURANJA VOZILA, LICA I IMOVINE", 46L);
        createDepartmentUnit("SLUŽBA ODRŽAVANJA VOZILA, MAŠINA I POMAGALA ZA RAD", 17L);
        createDepartmentUnit("ODELJENJE AUTOMEHANIČARA", 17L);
        createDepartmentUnit("ODELJENJE MAŠINSKE PRIPREME", 17L);
        createDepartmentUnit("ODELJENJE AUTOELEKTRIČARA", 17L);
        createDepartmentUnit("ODELJENJE AUTOBRAVARA - AUTOLIMARA", 17L);
        createDepartmentUnit("ODELJENJE AUTOLAKIRERA", 17L);
        createDepartmentUnit("ODELJENJE OPRAVKI OPREME", 17L);
        createDepartmentUnit("ODELJENJE ZA ODRŽAVANJE MAŠINA I INSTALACIJA", 17L);
        createDepartmentUnit("ODELJENJE ODRŽAVANJA AUTO-GUMA", 17L); // ******************** 60
        createDepartmentUnit("TEHNIČKO RAZVOJNA SLUŽBA", 17L);
        createDepartmentUnit("ODELJENJE KOTLARNICE I PARNIH INSTALACIJA", 17L);
        createDepartmentUnit("ODELJENJE ZA ODRŽAVANJE GRAĐEVINSKE MEHANIZACIJE", 17L);
        createDepartmentUnit("ODELJENJE GRAĐEVINSKIH POSLOVA", 17L);
        createDepartmentUnit("SLUŽBA DEFINISANJA KVALITETA", 17L);
        createDepartmentUnit("SLUŽBA UPRAVLJANJA INTEGRISANIM ŠIFARSKIM SISTEMOM", 17L);
        createDepartmentUnit("KORISNIČKI SERVIS", 2L);
        createDepartmentUnit("SEKTOR ZAŠTITE ŽIVOTNE SREDINE", 1L);
        createDepartmentUnit("ODELJENJE TEHNIČKO-LOGISTIČKIH POSLOVA", 68L);
        createDepartmentUnit("ODELJENJE ADMINISTRATIVNIH POSLOVA", 68L); // ******************** 70
        createDepartmentUnit("SLUŽBA TERENSKIH POSLOVA", 68L);
        createDepartmentUnit("SLUŽBA MONITORINGA", 68L);
        createDepartmentUnit("SLUŽBA POSLOVA LABORATORIJSKIH ISPITIVANJA", 68L);
        createDepartmentUnit("ODELJENJE TERENSKOG ODRŽAVANJA", 10L);
        createDepartmentUnit("ODELJENJE PISARNICE", 35L);
        createDepartmentUnit("SLUŽBA GARAŽNO TEHNIČKIH POSLOVA", 10L);
        createDepartmentUnit("ODELJENJE HEMIJSKIH ISPITIVANJA", 68L);
        createDepartmentUnit("ODELJENJE BIOLOŠKIH ISPITIVANJA", 68L);
        createDepartmentUnit("POSLOVI I ZADACI NA NIVOU PREDUZEĆA", 1L);
        createDepartmentUnit("ODELJENJE PRIPREMANJA I SERVIRANJA ISHRANE ZAPOSLENIH", 35L); // ********************** 80
        createDepartmentUnit("ODELJENJE POSLOVA OBAVEZA, PROMETA NOVCA I HARTIJA OD VREDNOSTI", 41L);
        createDepartmentUnit("ODELJENJE BILANSNIH POSLOVA", 41L);
        createDepartmentUnit("ODELJENJE ANALITIČKIH POSLOVA", 41L);
        createDepartmentUnit("ODELJENJE POSLOVA OBRAČUNA ZARADA", 41L);
        createDepartmentUnit("ODELJENJE POSLOVA POREZA, PRIHODA I POTRAŽIVANJA", 41L);
        createDepartmentUnit("SLUŽBA FINANSIJSKOG UPRAVLJANJA I KONTROLE", 41L);
        createDepartmentUnit("ODELJENJE TEHNIČKIH USLUGA", 35L);
    }

    private void createDepartmentUnit(String code, Long parentId) {
        DepartmentUnit departmentUnit = new DepartmentUnit();
        departmentUnit.setName(code);
        departmentUnit.setCode(code);
        departmentUnit.setDescription("");
        if (parentId != null && departmentUnitService.findById(parentId).isPresent()) {
            departmentUnit.setParent(departmentUnitService.findById(parentId).orElse(null));
        }

        departmentUnitService.save(departmentUnit);
    }

    private void createPermissions() {
        // User permissions
        createPermission("can_view_user");
        createPermission("can_create_user");
        createPermission("can_delete_user");
        createPermission("can_update_user");
        createPermission("can_import_users");
        createPermission("can_update_password");

        // Shift permissions
        createPermission("can_view_shift");
        createPermission("can_create_shift");
        createPermission("can_delete_shift");
        createPermission("can_update_shift");

        // Role permissions
        createPermission("can_view_role");
        createPermission("can_create_role");
        createPermission("can_delete_role");
        createPermission("can_update_role");

        // Permission permissions
        createPermission("can_view_permission");
        createPermission("can_create_permission");
        createPermission("can_delete_permission");
        createPermission("can_update_permission");

        // Timetable permissions
        createPermission("can_view_timetable");
        createPermission("can_create_timetable");
        createPermission("can_delete_timetable");
        createPermission("can_update_timetable");

        // DepartmentUnit permissions
        createPermission("can_view_department_unit");
        createPermission("can_create_department_unit");
        createPermission("can_delete_department_unit");
        createPermission("can_update_department_unit");

        // Device permissions
        createPermission("can_view_device");
        createPermission("can_create_device");
        createPermission("can_delete_device");
        createPermission("can_update_device");

        // Location permissions
        createPermission("can_view_location");
        createPermission("can_create_location");
        createPermission("can_delete_location");
        createPermission("can_update_location");

        // Card permissions
        createPermission("can_view_card");
        createPermission("can_create_card");
        createPermission("can_delete_card");
        createPermission("can_update_card");

        createPermission("can_create");
        createPermission("can_update");
        createPermission("can_delete");
        createPermission("can_view");
        
        createPermission("can_view_daily_report");
        createPermission("can_view_individual_report");
        createPermission("can_view_attendance_report");
        createPermission("can_view_work_hours_calculation_report");
        createPermission("can_view_check_in_check_out_status_report");
        createPermission("can_view_leaving_arrival_report");
        createPermission("can_view_pause_report");
    }

    private void createPermission(String name) {
        Permission permission = new Permission();
        permission.setName(name);
        permissionService.save(permission);
    }

    private void createLocations() {
        createLocation("Direkcija", "Industrijska 1");
        createLocation("Pogon 2", "Radnička 12");
        createLocation("Pogon 4", "Fabrička 21");
        createLocation("Pogon 12", "Skladišna 3");
        createLocation("Pogon 3", "Zanatska 8");
        createLocation("Pogon 5", "Magacinska 14");
        createLocation("Pogon Auto Baza", "Servisna 2");
        createLocation("Pogon 9", "Pogonska 7");
        createLocation("Pogon 7", "Logistička 19");
        createLocation("Pogon 8", "Tehnička 11");
        createLocation("Pogon 10", "Proizvodna 4");
        createLocation("Pogon 13", "Radionička 9");
        createLocation("Magacin", "Glavna 16");
        createLocation("Servis", "");
    }

    private Location createLocation(String name, String street) {
        Location location = new Location();
        location.setAddress(street);
        location.setCity("Belgrade");
        location.setDescription("Test description");
        location.setName(name);
        location.setPhone("011 123 456");
        return locationService.save(location);
    }

    private void createDevice(Location location, String code) {
        Device device = new Device();
        device.setActive(true);
        device.setCode("TEST 0" + code);
        device.setLocation(location);
        deviceService.save(device);
        device.setHardwareIdentifier(device.getId().toString());
        deviceService.save(device);
    }

    private void createDevices(Location location) {
        for (int i = 0; i < 99; i++) {
            createDevice(location, String.valueOf(i));
        }
    }

    private List<User> createUsers(List<Shift> shifts) throws IOException {
        List<User> data = new ArrayList<>();
        String usersRaw = new String(Files.readAllBytes(Path.of("src/main/resources/mock-data.txt")));
        usersRaw = usersRaw.replace("),(", "),,,(");
        String[] users = usersRaw.split(",,,");
        List<Integer> rfids = new ArrayList<>(DataSeederUtils.generateUniqueEightDigitNumbers(users.length + 1)); 
        int counter = 0;
        for (String user : users) {
            user = user.replace("(", "");
            user = user.replace(")", "");
            String[] userInformations = user.split(",");
            // userInformations index meaning
            // 0 - id
            // 1 - email
            // 2 - created
            // 3 - firstName
            // 4 - lastName
            // 5 - parentName
            // 6 - recordNumber
            // 7 - birthday
            // 8 - jmbg
            // 9 - rfid
            // 10 - languageId
            // 11 - structureId
            // 12 - workingPlace
            // 13 - title
            // 14 - officeNumber
            // 15 - avatar
            // 16 - shiftStart
            // 17 - ShiftEnd
            
            data.add(createUser(userInformations[3].replace("'", ""), userInformations[4].replace("'", ""), userInformations[5].replace("'", ""), userInformations[6].replace("'", ""), userInformations[12].replace("'", ""), rfids.get(counter), shifts));
            counter++;
        }
        return data;
    }
    
    private void createDevices() throws IOException{
        String devicesRaw = new String(Files.readAllBytes(Path.of("src/main/resources/device-mock-data.txt")));
        devicesRaw = devicesRaw.replace("),(", "),,,(");
        String[] devices = devicesRaw.split(",,,");
        for (String device : devices) {
        	device = device.replace("(", "");
        	device = device.replace(")", "");
            String[] deviceInfo = device.split(",");
            Device deviceObj = new Device();
            deviceObj.setActive(true);
            deviceObj.setLocation(locationService.findById(Long.valueOf(deviceInfo[1])).get());
            deviceObj.setCode(deviceInfo[2].replace("'", ""));
//            deviceObj.setHardwareIdentifier(deviceInfo[3].replace("'", ""));
            deviceObj.setDescription(deviceInfo[4].replace("'", ""));
            deviceService.save(deviceObj);
            deviceObj.setHardwareIdentifier(deviceObj.getId().toString());
            deviceService.save(deviceObj);
        }
    }


    private User createUser(String firstName, String lastName, String parentName, String recordNumber, String jobTitle, int rfid, List<Shift> shifts) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setParentName(parentName);
        user.setRecordNumber(recordNumber);
        user.setJobTitle(jobTitle);
        user.setUserType(UserType.EMPLOYEE);
        DepartmentUnit departmentUnit = departmentUnitService.findById(ThreadLocalRandom.current().nextLong(1, 86)).orElse(null);
        user.setDepartmentUnit(departmentUnit);
        Card card = new Card();
        card.setActive(true);
        card.setCardType(CardType.PERSONAL);
        card.setRfid(String.valueOf(rfid));
        Set<Shift> shiftSet = new HashSet<>();
        shiftSet.add(shifts.get(ThreadLocalRandom.current().nextInt(5)));
        shiftSet.add(shifts.get(ThreadLocalRandom.current().nextInt(5)));
        shiftSet.add(shifts.get(ThreadLocalRandom.current().nextInt(5)));
        
        user.setShifts(shiftSet);
        userService.save(user);
        card.setUser(user);
        cardService.save(card);
        
        return user;
    }

}
