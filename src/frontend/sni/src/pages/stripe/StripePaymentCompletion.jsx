export default function StripePaymentCompletion() {
  return (
    <div>
      <h1>Thank You!</h1>
      <p className={"font-italic"}>Your payment was successful.</p>
      <a href={'/dashboard'}>Back to dashboard.</a>
    </div>
  )
}
