import { StyleSheet, View, Button, Alert } from 'react-native';
import {
  schedule,
  SchedulerTypes,
  TimeUnits,
  WorkerPolicy,
  cancel,
} from 'react-native-background-sync';
import BackgroundCall from './task-manager';

export default function App() {
  const scheduleOneTime = async () => {
    const params = {
      taskKey: 'Onetime',
      type: SchedulerTypes.oneTime,
    };
    const result = await schedule(params, async () => {
      BackgroundCall(params.taskKey);
    });
    console.log('Result:', result);
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
    };
    const result = await schedule(params, async () => {
      BackgroundCall(params.taskKey);
    });
    console.log('Result:', result);
  };

  const cancelTask = async (taskKey: string) => {
    const response = await cancel(taskKey);
    Alert.alert(`Cancelled task for ${taskKey} : ${response}`);
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
