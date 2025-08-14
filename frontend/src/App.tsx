import React, {useCallback, useEffect, useState} from 'react';
import './App.css';

interface Product {
    id: number;
    name: string;
    price: number;
    stock: number;
    active: boolean;
}

interface ProductPage {
    content: Product[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
}

interface CartItem {
    productId: number;
    productName: string;
    price: number;
    quantity: number;
    stock: number;
}

interface OrderItem {
    productId: number;
    quantity: number;
}

interface OrderRequest {
    items: OrderItem[];
}

interface StockError {
    productId: number;
    available: number;
    productName?: string;
}

interface OrderResponse {
    id: number;
    createAt: string;
    total: number;
    items: Array<{
        productId: number;
        productName: string;
        quantity: number;
        unitPrice: number;
        lineTotal: number;
    }>;
}

const API_BASE_URL = 'http://localhost:8080/api/v1';

function App() {
    const [products, setProducts] = useState<Product[]>([]);
    const [cart, setCart] = useState<CartItem[]>([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [currentPage, setCurrentPage] = useState<number>(0);
    const [totalPages, setTotalPages] = useState<number>(0);
    const [isLoading, setIsLoading] = useState(false);
    const [isCartOpen, setIsCartOpen] = useState(false);
    const [message, setMessage] = useState<{ type: 'success' | 'error', content: string } | null>(null);
    const [stockErrors, setStockErrors] = useState<StockError[]>([]);

    const PAGE_SIZE = 6;

    const fetchProducts = useCallback(async (search: string, page: number) => {
        setIsLoading(true);
        try {
            const response = await fetch(
                `${API_BASE_URL}/products?search=${encodeURIComponent(search)}&page=${page}&size=${PAGE_SIZE}`
            );

            if (!response.ok) {
                throw new Error('Erro ao carregar produtos');
            }

            const data: ProductPage = await response.json();
            setProducts(data.content);
            // @ts-ignore
            setTotalPages(data.totalPages);
        } catch (error) {
            console.error('Erro ao buscar produtos: ', error);
            setMessage({type: 'error', content: 'Erro ao carregar produtos'});
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        const timeoutId = setTimeout(() => {
            setCurrentPage(0);
            fetchProducts(searchTerm,0);
        }, 300);

        return () => clearTimeout(timeoutId);
    }, [searchTerm, fetchProducts]);

    useEffect(() => {
        fetchProducts(searchTerm, currentPage);
    }, [currentPage, searchTerm, fetchProducts]);

    const addToCart = (product: Product) => {
        setCart(prevCart => {
            const existingItem = prevCart.find(item => item.productId === product.id);

            if (existingItem) {
                if (product.stock <= 0) {
                    setMessage({type: 'error', content: 'Quantidade máxima atingida'});
                    return prevCart;
                }
                return prevCart.map(item =>
                item.productId === product.id
                ? { ...item, quantity: item.quantity + 1 }
                : item
                );
            } else {
                if (product.stock === 0) {
                    setMessage({type: 'error', content: 'Produto fora de estoque'});
                    return prevCart;
                }
                return [...prevCart, {
                    productId: product.id,
                    productName: product.name,
                    price: product.price,
                    quantity: 1,
                    stock: product.stock
                }];
            }
        });

        setProducts(prevProducts =>
            prevProducts.map(item =>
                item.id === product.id
                    ? { ...item, stock: item.stock - 1}
                    : item
            )
        );
    };

    const removeFromCart = (productId: number) => {
        setCart(prevCart => {
            const existingItem = prevCart.find(item => item.productId === productId);

            if (existingItem && existingItem.quantity > 1) {
                return prevCart.map(item =>
                item.productId === productId
                ? { ...item, quantity: item.quantity - 1}
                : item
                );
            } else {
                return prevCart.filter(item => item.productId !== productId);
            }
        });

        setProducts(prevProducts =>
            prevProducts.map(item =>
                item.id === productId
                    ? { ...item, stock: item.stock + 1}
                    : item
            )
        );
    };

    const removeItemCompletely = (productId: number) => {
        const itemToRemove = cart.find(item => item.productId === productId);
        if (itemToRemove) {
            setProducts(prevProducts =>
                prevProducts.map(item =>
                    item.id === productId
                        ? { ...item, stock: item.stock + itemToRemove.quantity}
                        : item
                )
            );
        }
        setCart(prevCart => prevCart.filter(item => item.productId !== productId));
    };

    const getCartTotal = () => {
        return cart.reduce((total, item) => total + (item.price * item.quantity), 0);
    };
    const getCartItemCount = () => {
        return cart.reduce((total, item) => total + item.quantity, 0);
    };

    const checkout = async () => {
        if (cart.length === 0) {
            setMessage({type: 'error', content: 'Carrinho Vazio'});
            return;
        }

        const orderRequest: OrderRequest = {
            items: cart.map(item => ({
                productId: item.productId,
                quantity: item.quantity
            }))
        };

        try {
            const response = await fetch(`${API_BASE_URL}/orders`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(orderRequest)
            });

            if (response.status === 409) {
                const stockErrorsData: StockError[] = await response.json();
                setStockErrors(stockErrorsData);
                setMessage({type: 'error', content: 'Alguns itens não possuem estoque suficiente'});
                return;
            }

            if (!response.ok) {
                throw new Error('Erro ao finalizar pedido');
            }

            const orderData: OrderResponse = await  response.json();
            setMessage({
                type: 'success',
                content: `Pedido realizado com sucesso! ID: ${orderData.id}`
            });
            setCart([]);
            setStockErrors([]);
            setIsCartOpen(false);

            fetchProducts(searchTerm, currentPage);
        } catch (error) {
            console.error('Erro no checkout: ', error);
            setMessage({type:'error', content: 'Erro ao finalizar pedido'});
        }
    };

    const clearMessage = () => {
        setMessage(null)
        setStockErrors([]);
    };

    return (
        <div className="app">
            <header className="app-header">
                <h1>Catálogo de Produtos</h1>
                <button
                    className="cart-button"
                    onClick={() => setIsCartOpen(!isCartOpen)}
                    aria-label={`Abrir carrinho com ${getCartItemCount()} itens`}
                >
                    🛒 Carrinho ({getCartItemCount()})
                </button>
            </header>

            {message && (
                <div className={`message ${message.type}`}>
                    <span>{message.content}</span>
                    <button
                        onClick={clearMessage}
                        aria-label="Fechar mensagem"
                    >
                        ❎
                    </button>
                </div>
            )}

            {stockErrors.length > 0 && (
                <div className="stock-errors">
                    <h3>Itens indisponíveis:</h3>
                    <ul>
                        {stockErrors.map(error => (
                            <li key={error.productId}>
                                {error.productName || `Produto ${error.productId}`}:
                                apenas {error.available} disponível(is)
                            </li>
                            ))}
                    </ul>
                </div>
            )}

            <main className="main-content">
                <div className="search-container">
                    <input
                        type="text"
                        placeholder="Buscar produtos..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="search-input"
                        aria-label="Buscar produtos"
                    />
                </div>

                <div className="products-container">
                    {isLoading ? (
                        <div className="loading">Carregando produtos...</div>
                    ) : (
                        <>
                            <div className="products-grid">
                                {products.map(product => (
                                    <div key={product.id} className="product-card">
                                        <h3>{product.name}</h3>
                                        <p className="product-price">R$ {product.price.toFixed(2)}</p>
                                        <p className="product-stock">
                                            Estoque: {product.stock} unidade(s)
                                        </p>
                                        <button
                                            onClick={() => addToCart(product)}
                                            disabled={product.stock === 0}
                                            className="add-to-cart-btn"
                                            aria-label={`Adicionar ${product.name} ao carrinho`}
                                        >
                                            {product.stock === 0 ? 'Fora de Estoque' : 'Adicionar ao Carrinho'}
                                        </button>
                                    </div>
                                ))}
                            </div>

                            <div className="pagination">
                                <button
                                    onClick={() => setCurrentPage(prev => Math.max(0, prev - 1))}
                                    disabled={currentPage === 0}
                                    aria-label="Página anterior"
                                    >
                                    Anterior
                                </button>
                                <span>
                                    Página {currentPage +1} de {totalPages}
                                </span>
                                <button
                                    onClick={() => setCurrentPage(prev => Math.min(totalPages - 1, prev + 1))}
                                    disabled={currentPage === totalPages - 1}
                                    aria-label="Próxima página"
                                >
                                    Próxima
                                </button>
                            </div>
                        </>
                    )}
                </div>
            </main>

            {isCartOpen && (
                <div className="cart-overlay">
                    <div className="cart-sidebar">
                        <div className="cart-header">
                            <h2>Carrinho</h2>
                            <button
                                onClick={() => setIsCartOpen(false)}
                                className="close-cart"
                                aria-label="fechar carrinho"
                            >
                                ❎
                            </button>
                        </div>

                        <div className="cart-content">
                            {cart.length === 0 ? (
                                <p>Carrinho vazio</p>
                            ) : (
                                <>
                                    {cart.map(item => (
                                        <div key={item.productId} className="cart-item">
                                            <h4>{item.productName}</h4>
                                            <p>R$ {item.price.toFixed(2)}</p>
                                            <div className="quantity-controls">
                                                <button
                                                    onClick={() => removeFromCart(item.productId)}
                                                    aria-label={`Diminuir quantidade de ${item.productName}`}
                                                >
                                                 -
                                                </button>
                                                <span>{item.quantity}</span>
                                                <button
                                                    onClick={() => addToCart({
                                                        id: item.productId,
                                                        name: item.productName,
                                                        price: item.price,
                                                        stock: item.stock,
                                                        active: true
                                                    })}
                                                    disabled={item.quantity >= item.stock}
                                                    aria-label={`Adicionar quantidade de ${item.productName}`}
                                                    >
                                                    +
                                                </button>
                                            </div>
                                            <p className="item-total">
                                                Subtotal: R$ {(item.price * item.quantity).toFixed(2)}
                                            </p>
                                            <button
                                                onClick={() => removeItemCompletely(item.productId)}
                                                className="remove-item"
                                                aria-label={`Remover ${item.productName} do carrinho`}
                                            >
                                             Remover
                                            </button>
                                        </div>
                                    ))}

                                    <div className="cart-total">
                                        <h3>Total: R$ {getCartTotal().toFixed(2)}</h3>
                                        <button
                                            onClick={checkout}
                                            className="checkout-btn"
                                            aria-label="Finalizar pedido"
                                        >
                                            Finalizar Pedido
                                        </button>
                                    </div>
                                </>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default App;
