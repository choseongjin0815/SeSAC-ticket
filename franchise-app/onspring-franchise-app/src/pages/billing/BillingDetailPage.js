import React, { useState } from "react";
import BillingDetailsComponent from "../../component/billing/BillingDetailsComponent";

const BillingDetailPage = ({route}) => {
  const {month} = route.params;
  return (
    <BillingDetailsComponent month={month}/>
  );
};

export default BillingDetailPage;

