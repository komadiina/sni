import { useSearchParams } from "react-router-dom";


export default function StripePaymentCompletion(props) {
  const [queryParams, setQueryParams] = useSearchParams();
  const success = queryParams.get('redirect_status') === 'succeeded';

  if (!success) return <p>Payment failed.</p>;
  return (
    <div>
      <h1>Thank You!</h1>
      <p className={"font-italic"}>Your payment was successful.</p>
      <a href={'/dashboard'}>Back to dashboard.</a>
    </div>
  )
}
