const BackgroundCall = (data?: any) => {
  console.log(
    `Calling in background for task: `,
    JSON.stringify(data, null, 2)
  );
};

export default BackgroundCall;
