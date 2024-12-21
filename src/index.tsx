import { AppRegistry, NativeModules, Platform } from 'react-native';
import type { BackgroundSchedulerParams as IParams } from './types';

const LINKING_ERROR =
  `The package '@rn-native-utils/workmanager' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

export enum SchedulerTypes {
  periodic = 'PERIODIC',
  oneTime = 'ONE_TIME',
}

export enum WorkerPolicy {
  KEEP = 'KEEP',
  REPLACE = 'REPLACE',
  UPDATE = 'UPDATE',
}

export enum TimeUnits {
  HOUR = 'HOUR',
  DAY = 'DAY',
  SECOND = 'SECOND',
  MINUTES = 'MINUTES',
}

const BackgroundSync = NativeModules.BackgroundSync
  ? NativeModules.BackgroundSync
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const SupportedPlatforms = ['android'];

const isSupported = () => SupportedPlatforms.includes(Platform.OS);

export const schedule = async (
  params: IParams,
  callback: (data: string | Record<string, unknown> | boolean) => Promise<void>
): Promise<boolean> => {
  if (!isSupported()) {
    return false;
  }
  const { taskKey } = params;
  try {
    const response = await BackgroundSync.schedule(params);
    if (response) {
      AppRegistry.registerHeadlessTask(taskKey, () => callback);
    }
    return response;
  } catch (e) {
    throw new Error(e as string);
  }
};

export const cancel = async (taskKey: string): Promise<boolean> => {
  if (!isSupported()) {
    return false;
  }
  try {
    const response = await BackgroundSync.cancel(taskKey);
    return response;
  } catch (e) {
    throw new Error(e as string);
  }
};

export const disableAppIgnoringBatteryOptimization = (): boolean => {
  if (!isSupported()) {
    return false;
  }
  try {
    BackgroundSync.disableAppIgnoringBatteryOptimization();
  } catch (e) {
    throw new Error(e as string);
  }
  return true;
};

export type BackgroundSchedulerParams = IParams;
