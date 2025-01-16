import { useEffect, useState } from "react";
import api from "../../api/axios.js";
import BuyMenu from "../../components/BuyMenu.jsx";


const fetchBalance = async () => {
  let balance = 0.00;

  try {
    await Promise.all([
      api.instance.get('/balance/me').then((res) => {
        balance = res.data.additional.balance;
      })
    ]);
  } catch (err) {

  }

  return balance;
}

export default function DashboardUser(props) {
  const [balance, setBalance] = useState(0.00);

  useEffect(() => {
    const fetchData = async () => {
      return await fetchBalance();
    }

    fetchData().then(data => {
      setBalance(data);
    })
  }, []);

  const handleDeposit = async () => {
    const amount = document.querySelector("input").value.trim();

    try {
      await api.instance.post('/balance/me', { amount: amount }).then((res) => {
        setBalance(res.data.additional.balance);
      }).catch((err) => {
        console.log(err);
      });
    } catch (err) {
      console.log(err);
    }
  }

  return (
    <div className={ "" }>
      <div>
        <h1 className={ "text-2xl font-semibold" }
        >
          Balance: ${ balance.valueOf() } USD
        </h1>

        <div className={"flex flex-row items-center justify-center gap-4"}>
          <input placeholder={ "Amount" } className={ "p-2 rounded-xl" }/>
          <button className={"bg-green-200 p-2 rounded-xl bg-opacity-10"}
                  onClick={() => { handleDeposit(); }}
          >
            Deposit
          </button>
        </div>


      </div>

      <BuyMenu/>
    </div>
  )
}
