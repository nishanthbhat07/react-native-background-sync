# @rn-native-utils/background-sync

A React Native library for performing background data syncs in Android using Android's WorkManager.

## Installation

```sh
npm install @rn-native-utils/background-sync
```

or

```sh
yarn add @rn-native-utils/background-sync
```

## Supported Platforms
Currently, this library only supports Android.

## Usage
First, import the necessary functions and types from the library:

```ts
import { schedule, cancel, disableAppIgnoringBatteryOptimization, SchedulerTypes, WorkerPolicy, TimeUnits } from 'react-native-background-sync';
import type { BackgroundSchedulerParams } from 'react-native-background-sync';
```

### Scheduling a Background Task
To schedule a background task, use the schedule function:

```ts
// ...
const params: BackgroundSchedulerParams = {
  taskKey: 'uniqueTaskKey',
  // Add other required parameters
};

const callback = async () => {
  // Your background task logic here
};

try {
  const isScheduled = await schedule(params, callback);
  if (isScheduled) {
    console.log('Background task scheduled successfully');
  } else {
    console.log('Failed to schedule background task');
  }
} catch (error) {
  console.error('Error scheduling background task:', error);
}
```

### Canceling a Background Task 
To cancel a scheduled background task, use the cancel function:
```ts
try {
  const isCanceled = await cancel('uniqueTaskKey');
  if (isCanceled) {
    console.log('Background task canceled successfully');
  } else {
    console.log('Failed to cancel background task');
  }
} catch (error) {
  console.error('Error canceling background task:', error);
}
```

### Disabling Battery Optimization
To disable battery optimization for your app, which may be necessary for reliable background task execution, use the disableAppIgnoringBatteryOptimization function:

```ts
try {
  const isDisabled = disableAppIgnoringBatteryOptimization();
  if (isDisabled) {
    console.log('Battery optimization disabled successfully');
  } else {
    console.log('Failed to disable battery optimization');
  }
} catch (error) {
  console.error('Error disabling battery optimization:', error);
}
```

## API Reference

### Enums 
The library provides several enums for configuring background tasks:

```ts
enum SchedulerTypes {
periodic = 'PERIODIC',
oneTime = 'ONE_TIME',
}
```

```ts
enum WorkerPolicy {
KEEP = 'KEEP',
REPLACE = 'REPLACE',
UPDATE = 'UPDATE',
}
```

```ts
enum TimeUnits {
HOUR = 'HOUR',
DAY = 'DAY',
SECOND = 'SECOND',
MINUTES = 'MINUTES',
}
```

### Functions

1. `schedule(params: BackgroundSchedulerParams, callback: Task): Promise<boolean>`
   
   Schedules a background task with the given parameters and callback function.

2. `cancel(taskKey: string): Promise<boolean>`
   
   Cancels a scheduled background task with the specified task key.

3. `disableAppIgnoringBatteryOptimization(): boolean`
   
   Attempts to disable battery optimization for the app to ensure reliable background task execution.

## Types
The BackgroundSchedulerParams type is used to configure the background task. Refer to the library's type definitions for the exact structure and available options.
### BackgroundSchedulerParams

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| taskKey | string | Yes | Unique identifier for the background task |
| type | SchedulerTypes | Yes | Type of scheduler (PERIODIC or ONE_TIME) |
| maxRetryAttempts | number | No | Maximum number of retry attempts if task fails |
| retryDelay | number | No | Delay between retry attempts in milliseconds |
| taskTimeout | number | No | Maximum time allowed for task execution |
| allowedInForeground | boolean | No | Whether task can run while app is in foreground |
| syncInterval | number | No | Interval between periodic syncs |
| syncIntervalType | TimeUnits | No | Unit for syncInterval (SECOND, MINUTES, HOUR, DAY) |
| syncFlexTime | number | No | Flex time window for periodic syncs |
| syncFlexTimeType | TimeUnits | No | Unit for syncFlexTime |
| workerPolicy | WorkerPolicy | No | Policy for handling existing workers
| extras | Record<string, any> | No | Additional data to be passed to the background task |

## Error Handling
All functions in this library throw errors if something goes wrong during execution. It's recommended to wrap calls to these functions in try-catch blocks for proper error handling.

## Platform Specific Notes
- This library currently only supports Android. Calls to these functions on unsupported platforms (like iOS) will return `false` or do nothing.
- Make sure you have the necessary Android permissions and configurations set up in your project for background tasks to work properly.

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
