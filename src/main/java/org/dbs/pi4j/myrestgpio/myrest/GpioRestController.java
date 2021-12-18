package org.dbs.pi4j.myrestgpio.myrest;

import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import lombok.extern.slf4j.Slf4j;
import org.dbs.pi4j.myrestgpio.mygpio.MyGpioController;
import org.dbs.pi4j.myrestgpio.myrest.dto.PinDTO;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.Map.Entry;

import org.dbs.pi4j.myrestgpio.common.LogExecutionTime;

/**
 * Provides a REST interface with the pins.
 */
@RestController
@RequestMapping("/api/gpio")
@Slf4j
@CrossOrigin
public class GpioRestController {

    private final MyGpioController myGpioController;

    public GpioRestController(MyGpioController gpioManager) {
        this.myGpioController = gpioManager;
    }

    /**
     * Provision a GPIO as digital output pin.
     *
     * @param address The address of the GPIO pin.
     * @param name The name of the GPIO pin.
     * @return True if successful.
     */
    @PostMapping(path = "provision/digital/input/{address}/{name}", produces = "application/json")
    public boolean provisionDigitalInputPin(@PathVariable("address")  int address, @PathVariable("name")  String name) {
        return this.myGpioController.provisionDigitalInputPin(address, name);
    }

    /**
     * Provision a GPIO as digital output pin.
     *
     * @param address The address of the GPIO pin.
     * @param name The name of the GPIO pin.
     * @return True if successful.
     */
    @LogExecutionTime(value = "/api/gpio/", valueType = "POST provision/digital/output/{address}/{name}")
    @PostMapping(path = "provision/digital/output/{address}/{name}", produces = "application/json")
    public boolean provisionDigitalOutputPin(@PathVariable("address")  int address, @PathVariable("name")  String name) {
        return this.myGpioController.provisionDigitalOutputPin(address, name);
    }

    /**
     * Get the current state of the pins.
     *
     * @return {@link Collection} of {@link DigitalOutput}.
     */
    @LogExecutionTime(value = "/api/gpio/", valueType = "GET provision/list")
    @GetMapping(path = "provision/list", produces = "application/json")
    public List<PinDTO> getProvisionList() {
        log.info("GpioRestController::getProvisionList");

        final Map<Integer, Object> list = this.myGpioController.getProvisionedPins();

        final List<PinDTO> map = new ArrayList<>();

        for (final Entry<Integer, Object> entry : list.entrySet()) {
            final PinDTO pinDTO = initializePinDto(entry);

            if (entry.getValue() instanceof DigitalOutput) {
                valueForDigitalOutPut((DigitalOutput) entry.getValue(), pinDTO);
            }
            if (entry.getValue() instanceof DigitalInput) {
                valueForDigitalInput((DigitalInput) entry.getValue(), pinDTO);
            }

            pinDTO.setGlobalName("ProvisionedPin_" + entry.getKey());
            map.add(pinDTO);
        }

        return map;
    }

    private void valueForDigitalInput(DigitalInput digitalPin, PinDTO pinDTO) {
        pinDTO.setName(digitalPin.getName());
        pinDTO.setPinName(digitalPin.getId());
        pinDTO.setMode(digitalPin.type().name());
        pinDTO.setState(digitalPin.state().getValue());
    }

    private void valueForDigitalOutPut(DigitalOutput digitalPin, PinDTO pinDTO) {
        pinDTO.setName(digitalPin.getName());
        pinDTO.setPinName(digitalPin.getId());
        pinDTO.setMode(digitalPin.type().name());
        pinDTO.setState(digitalPin.state().getValue());
    }

    private PinDTO initializePinDto(Entry<Integer, Object> entry) {
        final PinDTO pinDTO  = new PinDTO();

        pinDTO.setAddress(entry.getKey());
        pinDTO.setType(entry.getValue().getClass().getName());
        return pinDTO;
    }

    /**
     * Get the current state of the pins.
     *
     * @return The value read from the pin
     */
    @LogExecutionTime(value = "/api/gpio/", valueType = "GET state/{address}")
    @GetMapping(path = "state/{address}", produces = "application/json")
    public String getState(@PathVariable("address") long address) {
        try {
            return String.valueOf(this.myGpioController.getState((int) address).getValue());
        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage());

            return ex.getMessage();
        }
    }

    /**
     * Set the state of a pin.
     *
     * @param address The address of the GPIO pin.
     * @param value Possible values: 1 (= PULL_DOWN), 2 (= PULL_UP), 0 and all other (= OFF).
     * @return True if successful.
     */
    @LogExecutionTime(value = "/api/gpio/", valueType = "POST digital/state/{address}/{value}")
    @PostMapping(path = "digital/state/{address}/{value}", produces = "application/json")
    public String setPinDigitalState(@PathVariable("address") long address, @PathVariable("value") long value) {
        try {
            return String.valueOf(this.myGpioController
                    .setPinDigitalState((int) address, (int) value));
        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage());

            return ex.getMessage();
        }
    }

    /**
     * Toggle a pin.
     *
     * @param address The address of the GPIO pin.
     * @return True if successful.
     */
    @LogExecutionTime(value = "/api/gpio/", valueType = "POST digital/toggle/{address}")
    @PostMapping(path = "digital/toggle/{address}", produces = "application/json")
    public boolean togglePin(@PathVariable("address") long address) {
        return this.myGpioController.togglePin((int) address);
    }

    /**
     * Pulse a pin.
     *
     * @param address The address of the GPIO pin.
     * @param duration The duration in milliseconds.
     * @return True if successful.
     */
    @LogExecutionTime(value = "/api/gpio/", valueType = "POST digital/pulse/{address}/{duration}")
    @PostMapping(path = "digital/pulse/{address}/{duration}", produces = "application/json")
    public boolean pulsePin(@PathVariable("address") long address, @PathVariable("duration") int duration) {
        return this.myGpioController.pulsePin((int) address, duration);
    }
}
