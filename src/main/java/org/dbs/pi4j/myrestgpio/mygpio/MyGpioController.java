package org.dbs.pi4j.myrestgpio.mygpio;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.platform.Platform;
import com.pi4j.util.Console;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dbs.pi4j.myrestgpio.common.PrintInfo;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
public class MyGpioController {

    private static final String THERE_IS_NO_PIN_PROVISIONED_AT_THE_GIVEN_ADDRESS = "There is no pin provisioned at the given address";
    private static final String THE_PROVISIONED_PIN_AT_THE_GIVEN_ADDRESS_IS_NOT_OF_THE_TYPE_GPIO_PIN_DIGITAL_OUTPUT = "The provisioned pin at the given address is not of the type GpioPinDigitalOutput";
    private final Context pi4j;
    private final Console console;

    @Getter
    private final Map<Integer, Object> provisionedPins;

    public MyGpioController() {
        pi4j = Pi4J.newAutoContext();
        console = new Console();
        provisionedPins = new HashMap<>();
        PrintInfo.printLoadedPlatforms(console, pi4j);
        PrintInfo.printDefaultPlatform(console, pi4j);
        PrintInfo.printProviders(console, pi4j);

        doOnetest();
    }

    private void doOnetest() {
        provisionDigitalOutputPin(23, "LED");
        setPinDigitalState(23,1);
    }

    public Platform getPlatform() {
        return pi4j.getPlatform();
    }

    /**
     * Provision a GPIO as digital output pin.
     *
     * @param address The address of the GPIO pin.13
     * @param name The name of the GPIO pin.
     * @return True if successful.
     */
    public boolean provisionDigitalOutputPin(final int address, final String name) {
        checkIfAdressAlreadyInUse(address);

        final DigitalOutputConfigBuilder provisionedPinConfig = DigitalOutput.newConfigBuilder(pi4j)
            .id(name)
            .name(name)
            .address(address)
            .shutdown(DigitalState.LOW)
            .initial(DigitalState.LOW).
            provider("pigpio-digital-output");

        this.provisionedPins.put(address, pi4j.create(provisionedPinConfig));

        return true;
    }


    /**
     * Provision a GPIO as digital input pin.
     *
     * @param address The address of the GPIO pin.
     * @param name The name of the GPIO pin.
     * @return True if successful.
     */
    public boolean provisionDigitalInputPin(final int address, final String name) {
        checkIfAdressAlreadyInUse(address);

        final var provisionedPinConfig = DigitalInput.newConfigBuilder(pi4j)
            .id("button")
            .name("Press button")
            .address(address)
            .pull(PullResistance.PULL_DOWN)
            .debounce(3000L)
            .provider("pigpio-digital-input");
        var provisionedPin = pi4j.create(provisionedPinConfig);

        if (provisionedPin != null) {
            this.provisionedPins.put(address, provisionedPin);
            return true;
        }

        return false;
    }

    /**
     * Get the state of the GPIO pin at the given address.
     *
     * @param address The address of the GPIO pin.
     * @return The {@link DigitalState}.
     */
    public DigitalState getState(final int address) {
        log.info("Get pin state requested for address {}", address);

        Object provisionedPin = this.provisionedPins.get(address);

        if (provisionedPin == null) {
            throw new IllegalArgumentException(THERE_IS_NO_PIN_PROVISIONED_AT_THE_GIVEN_ADDRESS);
        } else {
            if (provisionedPin instanceof DigitalInput) {
                return ((DigitalInput) provisionedPin).state();
            } else {
                throw new IllegalArgumentException("The provisioned pin at the given address is not of the type GpioPinDigitalInput");
            }
        }
    }

    /**
     * Set the state of a pin.
     *
     * @param address The address of the GPIO pin.
     * @param value The value, possible values 1 (= HIGH) or 0 and all other (= LOW)
     * @return True if successful.
     */
    public boolean setPinDigitalState(final int address, final int value) {
        log.info("Set pin digital state requested for address {} to value {}", address, value);

        Object provisionedPin = this.provisionedPins.get(address);

        if (provisionedPin == null) {
            throw new IllegalArgumentException(THERE_IS_NO_PIN_PROVISIONED_AT_THE_GIVEN_ADDRESS);
        } else {
            if (provisionedPin instanceof DigitalOutput) {
                if (value == 1) {
                    ((DigitalOutput) provisionedPin).high();
                } else {
                    ((DigitalOutput) provisionedPin).low();
                }

                return true;
            } else {
                throw new IllegalArgumentException(THE_PROVISIONED_PIN_AT_THE_GIVEN_ADDRESS_IS_NOT_OF_THE_TYPE_GPIO_PIN_DIGITAL_OUTPUT);
            }
        }
    }

    /**
     * Toggle a pin.
     *
     * @param address The address of the GPIO pin.
     * @return True if successful.
     */
    public boolean togglePin(final int address) {
        log.info("Toggle pin requested for address {}", address);

        Object provisionedPin = this.provisionedPins.get(address);

        if (provisionedPin == null) {
            throw new IllegalArgumentException(THERE_IS_NO_PIN_PROVISIONED_AT_THE_GIVEN_ADDRESS);
        } else {
            if (provisionedPin instanceof DigitalOutput) {
                ((DigitalOutput) provisionedPin).toggle();

                return true;
            } else {
                throw new IllegalArgumentException(THE_PROVISIONED_PIN_AT_THE_GIVEN_ADDRESS_IS_NOT_OF_THE_TYPE_GPIO_PIN_DIGITAL_OUTPUT);
            }
        }
    }

    /**
     * Pulse a pin for the given duration.
     *
     * @param address The address of the GPIO pin.
     * @param duration The duration in milliseconds.
     * @return True if successful.
     */
    public boolean pulsePin(final int address, final int duration) {
        log.info("Pulse pin requested for address {} with duration {}", address, duration);

        Object provisionedPin = this.provisionedPins.get(address);

        if (provisionedPin == null) {
            throw new IllegalArgumentException(THERE_IS_NO_PIN_PROVISIONED_AT_THE_GIVEN_ADDRESS);
        } else {
            if (provisionedPin instanceof DigitalOutput) {
                ((DigitalOutput) provisionedPin).pulse(duration, TimeUnit.MILLISECONDS);
                return true;
            } else {
                throw new IllegalArgumentException(THE_PROVISIONED_PIN_AT_THE_GIVEN_ADDRESS_IS_NOT_OF_THE_TYPE_GPIO_PIN_DIGITAL_OUTPUT);
            }
        }
    }

    private void checkIfAdressAlreadyInUse(int address) {
        if (this.provisionedPins.containsKey(address)) {
            log.error("There is already a provisioned pin at address {}", address);
            throw new IllegalArgumentException("There is already a provisioned pin at the given address");
        }
    }

}
