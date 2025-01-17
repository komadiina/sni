import { loadStripe } from "@stripe/stripe-js";
import { useEffect, useState } from "react";
import { Elements, CardElement, useStripe, useElements } from "@stripe/react-stripe-js";
import api from "../../api/axios.js";
import CheckoutForm from "./CheckoutForm.jsx";


export default function StripePayment(props) {
  const [publishableKey, setPublishableKey] = useState(null);
  const [stripePromise, setStripePromise] = useState(null);
  const [clientSecret, setClientSecret] = useState("")

  // fetch publishable key to load stripe
  useEffect(() => {
    const fetchConfig = async () => {
      const [apiResponse] = await Promise.all([
        api.instance.get("/stripe/config", { headers: { Authorization: `Bearer ${ api.getToken() }` }})
          .then((res) => { return res.data })
      ]);

      return apiResponse;
    };

    fetchConfig().then(apiResponse => {
      setPublishableKey(apiResponse.additional.publishableKey)
    });
  }, []);

  // load stripe on component mount
  useEffect(() => {
    if (publishableKey)
      setStripePromise(loadStripe(publishableKey))
  }, [publishableKey]);

  // initiate payment intent process from backend
  useEffect(() => {
      const fetchPaymentIntent = async () => {
        const [apiResponse] = await Promise.all([
          api.instance.post(
            "/stripe/payment-intent",
              { amount: 99, currency: "usd", paymentMethod: "pm_card_visa" },
              { headers: { Authorization: `Bearer ${ api.getToken() }` } }
            )
            .then((res) => { return res.data })
        ]);

        return apiResponse;
      };

      fetchPaymentIntent().then(apiResponse => {
        setClientSecret(apiResponse.client_secret)
      })
  }, [])

  return (
    <div>
      <h1>Test Stripe Payment</h1>
      {stripePromise && clientSecret && (
        <Elements
          stripe={stripePromise}
          options={{ clientSecret }}
        >
          <CheckoutForm/>
        </Elements>)}
    </div>
  )
}
