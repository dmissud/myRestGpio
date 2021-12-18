package org.dbs.pi4j.myrestgpio.myrest;

import com.pi4j.platform.Platform;
import lombok.extern.slf4j.Slf4j;
import org.dbs.pi4j.myrestgpio.common.NetworkInfo;
import org.dbs.pi4j.myrestgpio.mygpio.MyGpioController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.TreeMap;

/**
 * Provides a REST interface to expose all board info.
 *
 * Based on https://pi4j.com/1.2/example/system-info.html
 */
@RestController
@RequestMapping("/api/info")
@Slf4j
@CrossOrigin
public class InfoRestController {

    private final MyGpioController myGpioController;

    public InfoRestController(MyGpioController myGpioController) {
        this.myGpioController = myGpioController;
    }

    /**
     * Get the platform info.
     */
    @GetMapping(path = "platform", produces = "application/json")
    public Map<String, String> getPlatform() {
        Map<String, String> map = new TreeMap<>();

        Platform platform = myGpioController.getPlatform();


        try {
            map.put("id", platform.getId());
        } catch (NullPointerException ex) {
            map.put("id", "UNKNOWN");
        }

        map.put("description", platform.description());
        map.put("name", platform.name());

       return map;
    }

    /**
     * Get the OS info.
     */
    @GetMapping(path = "os", produces = "application/json")
    public Map<String, String> getOsInfo() {
        Map<String, String> map = new TreeMap<>();

//        try {
//            map.put("Name", myGpioController.getOsName());
//        } catch (Exception ex) {
//            log.error("OS name not available, error: {}", ex.getMessage());
//        }
//        try {
//            map.put("Version", myGpioController.getOsVersion());
//        } catch (Exception ex) {
//            log.error("OS version not available, error: {}", ex.getMessage());
//        }
//        try {
//            map.put("Architecture", myGpioController.getOsArch());
//        } catch (Exception ex) {
//            log.error("OS architecture not available, error: {}", ex.getMessage());
//        }
//        try {
//            map.put("FirmwareBuild", myGpioController.getOsFirmwareBuild());
//        } catch (Exception ex) {
//            log.error("OS firmware build not available, error: {}", ex.getMessage());
//        }
//        try {
//            map.put("FirmwareDate", myGpioController.getOsFirmwareDate());
//        } catch (Exception ex) {
//            log.error("OS firmware date not available, error: {}", ex.getMessage());
//        }

        return map;
    }

    /**
     * Get the Java info.
     */
    @GetMapping(path = "java", produces = "application/json")
    public Map<String, String> getJavaInfo() {
        Map<String, String> map = new TreeMap<>();

//        map.put("Vendor ", myGpioController.getJavaVendor());
//        map.put("VendorURL", myGpioController.getJavaVendorUrl());
//        map.put("Version", myGpioController.getJavaVersion());
//        map.put("VM", myGpioController.getJavaVirtualMachine());
//        map.put("Runtime", myGpioController.getJavaRuntime());

        return map;
    }

    /**
     * Get the network info.
     */
    @GetMapping(path = "network", produces = "application/json")
    public Map<String, String> getSystemInfo() {
        Map<String, String> map = new TreeMap<>();

        int counter = 0;

        try {
            map.put("Hostname", NetworkInfo.getHostname());
        } catch (Exception ex) {
            log.error("Network hostname not available, error: {}", ex.getMessage());
        }

        try {
            for (String ipAddress : NetworkInfo.getIPAddresses()) {
                map.put("IpAddress" + (counter++), ipAddress);
            }
        } catch (Exception ex) {
            log.error("IP address not available, error: {}", ex.getMessage());
        }

        try {
            counter = 0;
            for (String fqdn : NetworkInfo.getFQDNs()) {
                map.put("FQDN" + (counter++), fqdn);
            }
        } catch (Exception ex) {
            log.error("FQDN not available, error: {}", ex.getMessage());
        }

        try {
            counter = 0;
            for (String nameserver : NetworkInfo.getNameservers()) {
                map.put("Nameserver" + (counter++), nameserver);
            }
        } catch (Exception ex) {
            log.error("Nameserver not available, error: {}", ex.getMessage());
        }

        return map;
    }

}
