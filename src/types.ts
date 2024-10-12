import type {
  SchedulerTypes,
  TimeUnits,
  WorkerPolicy,
} from 'react-native-background-sync';

export interface BackgroundSchedulerParams {
  taskKey: string;
  type: SchedulerTypes;
  maxRetryAttempts?: number;
  retryDelay?: number;
  taskTimeout?: number;
  allowedInForeground?: boolean;
  syncInterval?: number;
  syncIntervalType?: TimeUnits;
  syncFlexTime?: number;
  syncFlexTimeType?: TimeUnits;
  workerPolicy?: WorkerPolicy;
}
