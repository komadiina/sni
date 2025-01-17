import { PaymentElement, useElements, useStripe } from "@stripe/react-stripe-js";
import { useState } from "react";


export default function CheckoutForm(props) {
  const stripe = useStripe();
  const elements = useElements();
  const [message, setMessage] = useState(null);
  const [isProcessing, setIsProcessing] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!stripe || !elements) {
      return;
    }

    setIsProcessing(true)
    const {error} = await stripe.confirmPayment({
      elements,
      confirmParams: {
        return_url: `${window.location.origin}/completion`
      }
    })

    if (error) {
      setMessage(error.message)
    }

    setIsProcessing(false)
  }

  return (
    <div className={"flex flex-auto items-center justify-center"}>
      <form id={"payment-form"} onSubmit={handleSubmit} className={"w-1/2"}>
        <PaymentElement/>

        <button id={"submit"} className={"p-2 bg-opacity-25 bg-green-200 my-4"} disabled={isProcessing}>
          <span id={"button-text"}>
            { isProcessing ? "Processing ..." : "Pay now"}
          </span>
        </button>
      </form>
    </div>
  );
}
