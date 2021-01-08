-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th1 08, 2021 lúc 09:49 AM
-- Phiên bản máy phục vụ: 10.3.16-MariaDB
-- Phiên bản PHP: 7.3.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `gudmarket`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `account`
--

CREATE TABLE `account` (
  `id_user` varchar(100) COLLATE utf8_vietnamese_ci NOT NULL,
  `password` varchar(100) COLLATE utf8_vietnamese_ci NOT NULL,
  `role` int(11) NOT NULL,
  `full_name` varchar(50) COLLATE utf8_vietnamese_ci DEFAULT NULL,
  `email` varchar(50) COLLATE utf8_vietnamese_ci DEFAULT NULL,
  `phone` varchar(15) COLLATE utf8_vietnamese_ci DEFAULT NULL,
  `address` varchar(200) COLLATE utf8_vietnamese_ci DEFAULT NULL,
  `money` decimal(10,2) DEFAULT NULL,
  `id_level` varchar(10) COLLATE utf8_vietnamese_ci DEFAULT NULL,
  `num_posted` int(11) DEFAULT NULL,
  `post_remain` int(11) DEFAULT NULL,
  `reg_date` date NOT NULL DEFAULT current_timestamp(),
  `block_to` date DEFAULT NULL,
  `level_to` date DEFAULT NULL,
  `last_impact` date DEFAULT NULL,
  `enabled` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_vietnamese_ci;

--
-- Đang đổ dữ liệu cho bảng `account`
--

INSERT INTO `account` (`id_user`, `password`, `role`, `full_name`, `email`, `phone`, `address`, `money`, `id_level`, `num_posted`, `post_remain`, `reg_date`, `block_to`, `level_to`, `last_impact`, `enabled`) VALUES
('114521247755400253912', '$2a$10$WeBrsEDU2/rMfpFXNcKA5.hbclof1juxXEpkEl8yV2KETli/8gz6u', 1, 'Long Nguyen', 'chimungvang002@gmail.com', NULL, NULL, '0.00', 'L01', 0, 3, '2020-12-06', NULL, NULL, NULL, 1),
('admin', '$2y$10$f7i8hYLSTHy5CZ0U8GluQODm5C/p.Gs/wGhQJMg6H7KTZXjtlQHKu', 0, 'Long Admin', 'admin', NULL, NULL, NULL, NULL, NULL, NULL, '2020-08-16', NULL, NULL, NULL, 1),
('U061220204031', '$2a$10$eI8zwQnf5RB5IcyonMl25OMUbMVccccYv72BtEI/pRPvM/GSRkOQG', 1, 'Long ne', 'longnd2c@gmail.com', '0358053153', '', '40.10', 'L02', 1, 92, '2020-12-06', NULL, '2021-03-07', '2020-12-07', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `category`
--

CREATE TABLE `category` (
  `id_cate` varchar(10) COLLATE utf8_vietnamese_ci NOT NULL,
  `cate_name` varchar(50) COLLATE utf8_vietnamese_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_vietnamese_ci;

--
-- Đang đổ dữ liệu cho bảng `category`
--

INSERT INTO `category` (`id_cate`, `cate_name`) VALUES
('C01', 'Fashion'),
('C02', 'Electronics'),
('C03', 'Pet'),
('C04', 'Home & Kitchen'),
('C05', 'Entertainment, Sports, Hobbies'),
('C06', 'Food'),
('C07', 'Home Electronics');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `level`
--

CREATE TABLE `level` (
  `id_level` varchar(10) COLLATE utf8_vietnamese_ci NOT NULL,
  `level_name` varchar(20) COLLATE utf8_vietnamese_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_vietnamese_ci;

--
-- Đang đổ dữ liệu cho bảng `level`
--

INSERT INTO `level` (`id_level`, `level_name`) VALUES
('L01', 'Normal'),
('L02', 'Silver'),
('L03', 'Gold'),
('L04', 'Platinum');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `post`
--

CREATE TABLE `post` (
  `id_post` varchar(50) COLLATE utf8_vietnamese_ci NOT NULL,
  `title` varchar(100) COLLATE utf8_vietnamese_ci NOT NULL,
  `id_user` varchar(100) COLLATE utf8_vietnamese_ci NOT NULL,
  `id_type` varchar(10) COLLATE utf8_vietnamese_ci NOT NULL,
  `description` text COLLATE utf8_vietnamese_ci NOT NULL,
  `img` varchar(500) COLLATE utf8_vietnamese_ci DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `contact` varchar(100) COLLATE utf8_vietnamese_ci NOT NULL,
  `date` date NOT NULL DEFAULT current_timestamp(),
  `priority` date DEFAULT NULL,
  `status` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_vietnamese_ci;

--
-- Đang đổ dữ liệu cho bảng `post`
--

INSERT INTO `post` (`id_post`, `title`, `id_user`, `id_type`, `description`, `img`, `price`, `contact`, `date`, `priority`, `status`) VALUES
('06122020_4843', 'Post 1', 'U061220204031', 'T12', '5465gf', '/webapp/images/posts/06122020_4843-img01.jpg@', '12.00', '01111125434', '2020-12-06', NULL, 0),
('19112020_2038', 'Zamzung Universe 20', '114521247755400253912', 'T21', '- 2TB ROM\r\n- 300 GB RAM\r\n- Snap 999', '/webapp/images/posts/19112020_2038-img01.jpg@', '3410.00', '0888756423', '2020-11-19', NULL, 1),
('19112020_2549', 'LGBT Shoes', '114521247755400253912', 'T12', '- LGBT Shoes Special Degisn\r\n- Very beautiful', '/webapp/images/posts/19112020_2549-img01.jpg@', '150.00', '01111125434', '2020-11-19', NULL, 1),
('19112020_3981', 'HUEWEI M80', '114521247755400253912', 'T21', '- 125 GB ROM\r\n- 64 GB RAM', '/webapp/images/posts/19112020_3981-img01.jpg@/webapp/images/posts/19112020_3981-img02.jpg@/webapp/images/posts/19112020_3981-img03.jpg@', '2500.00', '015531356', '2020-11-19', NULL, 1),
('19112020_5130', 'MZI GT 989', '114521247755400253912', 'T22', '- XTX 1990\r\n- 2TB SSD\r\n', '/webapp/images/posts/19112020_5130-img01.jpg@', '4000.00', '09999963322', '2020-11-19', NULL, 1),
('19112020_5629', 'Hehe', '114521247755400253912', 'T11', 'sdjhdfbjhg', '/webapp/images/posts/19112020_5629-img01.jpg@/webapp/images/posts/19112020_5629-img02.jpg@', '12.00', '01654616356', '2020-11-19', NULL, 1),
('19112020_5926', 'Snow Cat', '114521247755400253912', 'T32', '- Jump hight\r\n- Claw Face', '/webapp/images/posts/19112020_5926-img01.jpg@', '142.00', '0112454521', '2020-11-19', NULL, 1),
('19112020_6010', 'Husky Snow', '114521247755400253912', 'T31', '- Very Beautiful\r\n- Just Eat and Sleep', '/webapp/images/posts/19112020_6010-img01.jpg@/webapp/images/posts/19112020_6010-img02.jpg@/webapp/images/posts/19112020_6010-img03.jpg@', '452.00', '0477875541', '2020-11-19', NULL, 1),
('19112020_7523', 'LGBT Dress', '114521247755400253912', 'T11', '- LGBT Dress Special Design for LGBT Comunity\r\n- Confident showing gender', '/webapp/images/posts/19112020_7523-img01.jpg@', '200.00', '0999778554', '2020-11-19', NULL, 1),
('19112020_9062', 'Yellow Uncle', '114521247755400253912', 'T31', '- Very Smart\r\n- Just eat and sleep all days', '/webapp/images/posts/19112020_9062-img01.jpg@', '120.00', '04213465741', '2020-11-19', NULL, 1),
('19112020_9531', 'APhone 12 Pro Max Ultra', '114521247755400253912', 'T21', '- 125 GB RAM\r\n- 2TB ROM\r\n- PineApple I22', '/webapp/images/posts/19112020_9531-img01.jpg@', '2500.00', '08885674312', '2020-11-19', NULL, 1),
('19112020_9883', 'Zanon EOS 990', '114521247755400253912', 'T23', '- Zanon New', '/webapp/images/posts/19112020_9883-img01.jpg@', '2560.00', '08794561223', '2020-11-19', NULL, 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `token`
--

CREATE TABLE `token` (
  `id` int(11) NOT NULL,
  `token` varchar(100) COLLATE utf8_vietnamese_ci NOT NULL,
  `id_user` varchar(100) COLLATE utf8_vietnamese_ci NOT NULL,
  `expiryDate` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_vietnamese_ci;

--
-- Đang đổ dữ liệu cho bảng `token`
--

INSERT INTO `token` (`id`, `token`, `id_user`, `expiryDate`) VALUES
(31, '8a33a37f-f787-4186-a702-9f13df7e40aa', 'U061220204031', '2021-01-09'),
(32, '9d5191f6-f05c-409c-bcfe-3072875f7677', 'U061220204031', '2021-01-09');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `type`
--

CREATE TABLE `type` (
  `id_type` varchar(10) COLLATE utf8_vietnamese_ci NOT NULL,
  `type_name` varchar(50) COLLATE utf8_vietnamese_ci NOT NULL,
  `id_cate` varchar(10) COLLATE utf8_vietnamese_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_vietnamese_ci;

--
-- Đang đổ dữ liệu cho bảng `type`
--

INSERT INTO `type` (`id_type`, `type_name`, `id_cate`) VALUES
('T11', 'Clothes', 'C01'),
('T12', 'Shoes', 'C01'),
('T13', 'Jewelry', 'C01'),
('T14', 'Other', 'C01'),
('T21', 'Smartphone', 'C02'),
('T22', 'Laptop', 'C02'),
('T23', 'Camera', 'C02'),
('T24', 'Other', 'C02'),
('T31', 'Dog', 'C03'),
('T32', 'Cat', 'C03'),
('T33', 'Other', 'C03'),
('T41', 'Shelf', 'C04'),
('T42', 'Kitchen Tools', 'C04'),
('T43', 'Bed & Bedding', 'C04'),
('T44', 'Other', 'C04'),
('T51', 'Musical Instrument', 'C05'),
('T52', 'Book', 'C05'),
('T53', 'Sports', 'C05'),
('T54', 'Collectibles', 'C05'),
('T55', 'Other', 'C05'),
('T61', 'Fruit', 'C06'),
('T62', 'Candy', 'C06'),
('T63', 'Dry Food', 'C06'),
('T64', 'Other', 'C06'),
('T71', 'Refrigerator', 'C07'),
('T72', 'Washing Machine', 'C07'),
('T73', 'Air Conditioner', 'C07'),
('T74', 'Other', 'C07');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`id_user`);

--
-- Chỉ mục cho bảng `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id_cate`);

--
-- Chỉ mục cho bảng `level`
--
ALTER TABLE `level`
  ADD PRIMARY KEY (`id_level`);

--
-- Chỉ mục cho bảng `post`
--
ALTER TABLE `post`
  ADD PRIMARY KEY (`id_post`);

--
-- Chỉ mục cho bảng `token`
--
ALTER TABLE `token`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `type`
--
ALTER TABLE `type`
  ADD PRIMARY KEY (`id_type`),
  ADD KEY `id_cate` (`id_cate`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `token`
--
ALTER TABLE `token`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `type`
--
ALTER TABLE `type`
  ADD CONSTRAINT `type_ibfk_1` FOREIGN KEY (`id_cate`) REFERENCES `category` (`id_cate`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
