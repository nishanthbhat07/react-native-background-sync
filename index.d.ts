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
  extras?: Record<string, unknown>;
}

export function schedule(
  params: BackgroundSchedulerParams,
  callback: (data: string | Record<string, unknown> | boolean) => Promise<void>
): Promise<boolean>;

export function cancel(taskKey: string): Promise<boolean>;

export function disableAppIgnoringBatteryOptimization(): boolean;

export type { BackgroundSchedulerParams };
