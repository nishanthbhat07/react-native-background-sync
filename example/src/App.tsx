import { StyleSheet, View, Button, Alert } from 'react-native';
import {
  schedule,
  SchedulerTypes,
  TimeUnits,
  WorkerPolicy,
  cancel,
} from '@rn-native-utils/workmanager';

import BackgroundCall from './task-manager';

export default function App() {
  const scheduleOneTime = async () => {
    const params = {
      taskKey: 'Onetime',
      type: SchedulerTypes.oneTime,
      extras: {
        hello: 'Hi',
        user: 'John',
        id: 123,
        data: {
          city: 'Pune',
        },
      },
    };
    try {
      const result = await schedule(params, async (data) =>
        BackgroundCall(data)
      );
      console.log('Result:', result);
    } catch (e) {
      console.error(e);
    }
  };

  const schedulePeriodic = async () => {
    const params = {
      taskKey: 'periodic',
      type: SchedulerTypes.periodic,
      syncInterval: 15,
      syncIntervalType: TimeUnits.MINUTES,
      syncFlexTime: 5,
      syncFlexTimeType: TimeUnits.MINUTES,
      workerPolicy: WorkerPolicy.KEEP,
      extras: {
        hello: 'Hi',
        user: 'John',
        id: 123,
        data: {
          city: 'Pune',
        },
      },
    };
    const result = await schedule(params, async (data: any) => {
      BackgroundCall(data);
    });
    console.log('Result:', result);
  };

  const cancelTask = async (taskKey: string) => {
    try {
      const response = await cancel(taskKey);
      Alert.alert(`Cancelled task for ${taskKey} : ${response}`);
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <View style={styles.container}>
      <Button onPress={scheduleOneTime} title="Schedule One time" />
      <Button onPress={schedulePeriodic} title="Schedule Periodic" />
      <Button onPress={() => cancelTask('Onetime')} title="Cancel One time" />
      <Button onPress={() => cancelTask('periodic')} title="Cancel Periodic" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 30,
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
