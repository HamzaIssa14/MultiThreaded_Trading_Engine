# Trading Engine Prototype

## Overview

A trading engine is a crucial component of any trading platform, handling vital functions such as order management, order matching, and maintaining the order book. This project aims to prototype a model trading engine capable of supporting high concurrency through multithreading, efficiently matching buy and sell orders.

As of Dec 31st, 2023, this initial implementation focuses on the collection and order matching stages of the trading engine. The server processes incoming buy/sell orders from clients, initiates the order processing workflow, and concludes by filling orders fully or partially, cycling any partially filled orders back into the pending order queue.

## Workflow

![Trading Engine Process](Trading_Engine_Design_2023.PNG)

The trading engine operates through the following steps:

1. **Order Reception**: The server receives an `Order` object within an input stream from the Client.
2. **StockPipeline Initialization**: Conditionally, the server creates a `StockPipeline` for the `StockTicker` specified in the order. If an existing `StockPipeline` is in place, it is reused.
3. **Concurrency Management**: The server processes multiple orders concurrently, handling requests from various clients simultaneously.
4. **Order Processing**: The `StockPipeline`, a core component of the Trading Engine, adopts the Producer-Consumer model to manage order processing.
5. **Pending Order Queue**: Incoming orders are placed into a pending order buffer, awaiting categorization into buy or sell queues.
6. **Order Queuing**: Orders are routed to either the buy or sell order queue based on their type.
7. **Order Consumption**: The `OrderConsumer` object extracts orders from the queues for processing.
8. **Order Matching**: The `OrderConsumer` fetches orders from both the `SellOrderQueue` and `BuyOrderQueue` to optimally pair them.
9. **Order Matching Algorithm**: With the proper distribution of buy and sell orders, the `OrderConsumer` executes the `OrderMatchingAlgorithm`.
10. **Order Filling**: The algorithm fills the buy order and as many sell orders as possible. Partially filled sell orders are labeled `PARTIALLY_FILLED` and returned to the `SellOrderQueue` with the filled quantity deducted.
11. **Process Loop**: The order re-enters the process, designed with concurrency to ensure no step blocks any other step or user.

## Concurrency and Efficiency

This system is architected with concurrency at its core, ensuring efficient and non-blocking operations across all stages of order processing. The goal is to provide a robust and scalable trading engine that can handle a significant load of simultaneous user interactions.

## Getting Started

(Awaiting further updates)

## Contributing

(Awaiting further updates)

## License

(Awaiting further updates)

## Acknowledgments

(Awaiting further updates)

## Contact

(Awaiting further updates)

