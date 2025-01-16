import { useEffect, useState } from "react";
import api from "../api/axios.js";

const getProducts = async () => {
  let data = { };

  const response = await api.instance.get("https://localhost:8443/api/paypal/catalog")
    .then((response) => {
      data = response.data.map((product) => {
        return {
          id: product.id,
          name: product.name,
          description: product.description
        }
      })
    }).catch((err) => {
      if (err.status === 401 || err.status === 403) {
        window.location.href = '/token-expired'
        return;
      }

      console.error(err)
    })

  return data;
}

export default function BuyMenu() {
  const [cart, setCart] = useState([]);
  const [products, setProducts] = useState([]);

  useEffect( () => {
    async function fetchData() {
      const apiResponse = await getProducts();
      return apiResponse;
    }

    fetchData().then(apiResponse => {
      setProducts(apiResponse);
    })
  }, []);

  return (
    <div>
      {
        products.length &&
        products.map((product) => {
          return (
            <div className={"p-4 border-2 rounded-xl flex flex-row gap-4 justify-between items-start m-4"} key={product.id}>
              <div className={"flex flex-col items-start justify-start"}>
                <p className={"text-2xl font-bold"}>{ product.name }</p>
                <p className={"text-lg"}>{ product.description }</p>
              </div>


              <button className={ "right-0 align-middle p-4 border-2 border-white bg-white bg-opacity-15 text-black font-bold" }>Buy</button>
            </div>
          )
        })
      }
    </div>
  )
}
