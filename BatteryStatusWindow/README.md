# BatteryStatusDemo

# Battery Status Floating Window App README

## 1. Overview
This is an example application code used to display battery status information.
It achieves real-time presentation of various battery status parameters such as charging status, battery level, voltage, temperature, etc. in real time by creating a floating window. 
The application consists of two main Java files: `MainActivity.java` and `FloatingWindowService.java`.

## 2. File Structure

### 1. `MainActivity.java`
- This file is mainly responsible for starting the `FloatingWindowService` and registering a broadcast receiver to listen for the service stop broadcast. 
- When the service stop broadcast is received, it will end the current activity and exit the application.

### 2. `FloatingWindowService.java`
- This is the core service class of the application and is responsible for the following functions:
    - **Initialization and creation of the floating window**:
        - In the `onCreate` method, it sends a broadcast for battery status change and registers a broadcast receiver to listen for battery status change, power connection, and disconnection events.
        - The `setWindow` method is used to set the appearance and layout of the floating window, including setting window parameters, adding views, and handling click events.
        - The `setParams` method is used to set the layout parameters of the floating window, such as size, position, and type.
    - **Processing battery information**:
        - The `BatteryReceiver` is an internal broadcast receiver class used to receive broadcasts related to battery status changes.
        - The `updateInfo` method updates various battery status information according to the received Intent, including charging status, battery level, health status, voltage, temperature, etc.
        - The `printInfo` method is used to build and print the string of battery status information, and also performs verification and error handling of some data. It will display the battery status information in the `TextView` of the floating window and record error information in the log.
    - **Stopping the service**:
        - The `stopMyService` method is used to stop the service, including removing the floating window view, unregistering the broadcast receiver, and sending a service stop broadcast.

### 3. window.xml
- This file defines the layout of the application's floating window, which is User Interface Layout.


## 3. Function Details

### 1. Battery Status Broadcast Reception
- The `onReceive` method in the `BatteryReceiver` class processes different situations according to the `Action` of the received Intent:
    - If `ACTION_BATTERY_CHANGED`, it calls the `updateInfo` and `printInfo` methods to update and display the battery status information.
    - If `ACTION_POWER_CONNECTED`, in addition to updating and displaying information, it will also show a Toast prompt of "Start Charging!" and record relevant logs.
    - If `ACTION_POWER_DISCONNECTED`, it similarly updates and displays information, shows a Toast prompt of "Stop Charging!" and records logs.

### 2. Battery Information Update
- The `updateInfo` method extracts various battery status information from the Intent:
    - It obtains information such as charging status (`isCharging`), charging method (`usbCharge` and `acCharge`), battery level (`battery`), health status (`health`), voltage (`voltage`), temperature (`temperature`), battery technology (`technology`), whether the battery is present (`batteryPresent`), capacity (`capacity`), charge counter (`chargeCounter`), current current (`currentNow`), average current (`currentAverage`), energy counter (`energyCounter`), and charging remaining time (`chargeTimeRemaining`) through the `BatteryManager`. At the same time, it also obtains the power saving mode (`powerSaveMode`).

### 3. Battery Information Display and Error Handling
- The `printInfo` method builds the string of battery status information and performs verification and error handling of some data:
    - It first creates a `StringBuilder` to build the battery information string, and then adds each battery status information one by one.
    - For situations where some data values are out of the reasonable range (such as battery level, capacity, voltage, temperature, etc.), it will record error logs.
    - Finally, it sets the built battery information string to the `TextView` in the floating window for display and clears the `StringBuilder` for the next use.

### 4. Service Stopping
- The `stopMyService` method is used to stop the service:
    - Removes the floating window view.
    - Unregisters the broadcast receiver.
    - Sends a service stop broadcast.
    - Stops the service itself.

## 4. Notes and Potential Problems

### 1. Data Accuracy and Error Handling
- There is some data verification and error handling logic when obtaining and processing battery information. For example, for situations where some battery parameter values are out of the reasonable range, error logs will be recorded, but the processing method may need to be further optimized, such as providing a more friendly prompt to the user or using a more appropriate default value.
- On some devices, inaccurate or incorrect battery information may be obtained. For example, some battery property values may fail to be obtained (such as `currentAverage` and `energyCounter` sometimes returning `Long.MIN_VALUE`). This may be caused by differences in device hardware, drivers, or the system, and further analysis and solutions are needed.
- Inside the code, the determination of the abnormal value range of each property is specified, and the range values can be modified according to requirements. 

### 2. Floating Window Permissions
- In the Android system, creating a floating window requires specific permissions. 
- Ensure that the application has correctly declared the corresponding permissions in the manifest file and that the user has granted these permissions, otherwise the floating window may not be displayed normally.

### 3. Compatibility Issues
- There are some parts related to the Android version in the code, such as the setting of window types and the use of some specific APIs (such as the conditional judgment related to `Build.VERSION.SDK_INT`). 
- Different Android versions may have different implementations and support for these functions, and further testing and optimization may be needed to ensure normal operation on devices of different versions.

### 4. Broadcast Receiver Registration and Unregistration
- Correctly registering and unregistering the broadcast receiver in the `onCreate` and `onDestroy` methods is very important to avoid memory leaks and other potential problems. 
- Ensure that these operations are performed at the appropriate time.

### 5. String Processing and Display
- When building the battery information string, pay attention to the format and content accuracy of the string. 
- For example, the formatting and unit conversion of some battery parameters in the `printInfo` method may need to be further verified and optimized to ensure that the information displayed to the user is clear and accurate.

### 6. Theme Details
The application uses a custom theme defined in the `themes.xml` file called "TransparentTheme". 
This theme is based on `Theme.AppCompat.Light.NoActionBar`.

### 7. Window Background and Translucency
- The theme has two key properties set. The `android:windowBackground` is set to `@android:color/transparent`, making the background of the application's windows transparent. 
- The `android:windowIsTranslucent` is set to `true`, further enhancing the transparency or translucency effect.

### 8. Usage in MainActivity
- In the `Manifest.xml` file, the `MainActivity` uses this "TransparentTheme". 
- This gives the application a particular visual appearance, likely making it blend in with the device's screen or other elements in a more seamless way. 
- The use of this theme in combination with the layout defined in `window.xml` helps create a unique and user-friendly interface for displaying the battery status information in a floating window.

## 5. Usage
1. First, ensure that the Android project of the application has been correctly configured and built.
2. Install the application on the device.
3. After running the application, it will automatically create a floating window to display the battery status information.
4. When the battery status changes (such as charging status change, battery level change, etc.), the information in the floating window will be automatically updated.
5. The user can click the "OK" button in the floating window to stop the service and close the floating window.

## 6. Test

### 1. Machine 1
- **Device Model**: wizarPos SHWP_WIZARPOS_Q3 [Android 7.1.2, API 25]
- **Device Serial Number**: WP3500PQ20000002
- **Battery Model**: PE3416A
- **Rated Power**: 3.8V 3000mAh
- **Special Situations**:
    - CurrentNow has always been negative.
    - The values of CurrentAverage and EnergyCounter are abnormal.
    - The remaining charging time information takes a long time to obtain and is initially 0.

### 2. Machine 2
- **Device Model**: SHWP WIZARPOS_Q2 [Android 12, API 32]
- **Device Serial Number**: WP30002Q30000001
- **Battery Model**: WHB03-3000
- **Rated Power**: 3.8V 3000mAh
- **Special Situations**:
    - CurrentNow has always been negative.
    - The values of CurrentAverage, EnergyCounter, and ChargeCounter are abnormal.
    - The battery technology and remaining charging time information cannot be obtained.

### 3. Machine 3
- **Device Model**: wizarPos WIZARPOS_Q2 [Android 6.0.1, API 23]
- **Device Serial Number**: WP32601Q23200435
- **Battery Model**: WHB02-2600
- **Rated Power**: 7.2V 2600mAh
- **Special Situations**:
    - CurrentNow has always been negative and has a large absolute value.
    - The values of CurrentAverage, EnergyCounter, and ChargeCounter are abnormal.
    - The remaining charging time information cannot be obtained.
