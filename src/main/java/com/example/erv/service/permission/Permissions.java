package com.example.erv.service.permission;

public class Permissions {

    public interface EntityPermission {
        String getName();
    }

    public enum DepartmentUnit implements EntityPermission {
        READ("can_view_department_unit"),
        UPDATE("can_update_department_unit"),
        CREATE("can_create_department_unit"),
        DELETE("can_delete_department_unit");

        DepartmentUnit(String name) {
            this.name = name;
        }

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }

    public enum Card implements EntityPermission {
        READ("can_view_card"),
        UPDATE("can_update_card"),
        CREATE("can_create_card"),
        DELETE("can_delete_card");

        Card(String name) {
            this.name = name;
        }

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }

    public enum Device implements EntityPermission {
        READ("can_view_device"),
        UPDATE("can_update_device"),
        CREATE("can_create_device"),
        DELETE("can_delete_device");

        Device(String name) {
            this.name = name;
        }

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }

    public enum Location implements EntityPermission {
        READ("can_view_location"),
        UPDATE("can_update_location"),
        CREATE("can_create_location"),
        DELETE("can_delete_location");

        Location(String name) {
            this.name = name;
        }

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }

    public enum Permission implements EntityPermission {
        READ("can_view_permission"),
        UPDATE("can_update_permission"),
        CREATE("can_create_permission"),
        DELETE("can_delete_permission");

        Permission(String name) {
            this.name = name;
        }

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }

    public enum Role implements EntityPermission {
        READ("can_view_role"),
        UPDATE("can_update_role"),
        CREATE("can_create_role"),
        DELETE("can_delete_role");

        Role(String name) {
            this.name = name;
        }

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }

    public enum Settings implements EntityPermission {
        READ("can_view_settings"),
        UPDATE("can_update_settings"),
        CREATE("can_create_settings"),
        DELETE("can_delete_settings");

        Settings(String name) {
            this.name = name;
        }

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }

    public enum Shift implements EntityPermission {
        READ("can_view_shift"),
        UPDATE("can_update_shift"),
        CREATE("can_create_shift"),
        DELETE("can_delete_shift");

        Shift(String name) {
            this.name = name;
        }

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }

    public enum Timetable implements EntityPermission {
        READ("can_view_timetable"),
        UPDATE("can_update_timetable"),
        CREATE("can_create_timetable"),
        DELETE("can_delete_timetable");

        Timetable(String name) {
            this.name = name;
        }

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }

    public enum User implements EntityPermission {
        READ("can_view_user"),
        UPDATE("can_update_user"),
        CREATE("can_create_user"),
        DELETE("can_delete_user"),
        IMPORT("can_import_users"),
    	UPDATE_PASSWORD("can_update_password");

        User(String name) {
            this.name = name;
        }

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }
    
    public enum Report implements EntityPermission {
    	VIEW_DAILY_REPORT("can_view_daily_report"),
    	VIEW_INDIVIDUAL_REPORT("can_view_individual_report"),
    	VIEW_ATTENDANCE_REPORT("can_view_attendance_report"),
    	VIEW_WORKING_HOURS_CALCULATION_REPORT("can_view_work_hours_calculation_report"),
    	VIEW_CHECK_IN_CHECK_OUT_STATUS_REPORT("can_view_check_in_check_out_status_report"),
    	VIEW_LEAVING_ARRIVAL_REPORT("can_view_leaving_arrival_report"),
    	VIEW_PAUSE_REPORT("can_view_pause_report");
    	
    	Report(String name) {
            this.name = name;
        }

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }
}
